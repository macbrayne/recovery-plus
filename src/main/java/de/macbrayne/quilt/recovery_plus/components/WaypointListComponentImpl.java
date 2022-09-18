package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.misc.Waypoint;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WaypointListComponentImpl implements WaypointListComponent, AutoSyncedComponent, PlayerComponent<WaypointListComponent> {
	public static final Logger LOGGER = LoggerFactory.getLogger("recovery_plus");
	private final Player provider;
	int progress = 0;
	private List<Waypoint> lastDeath = new ArrayList<>();
	private List<Waypoint> workingCopy = new ArrayList<>(); // Server Only

	public WaypointListComponentImpl(Player provider) {
		this.provider = provider;
	}

	@Override
	public List<Waypoint> getLastDeath() {
		return lastDeath;
	}

	@Override
	public List<Waypoint> getWorkingCopy() {
		return workingCopy;
	}

	@Override
	public int getProgress() {
		return progress;
	}

	@Override
	public void setWorkingCopy(List<Waypoint> workingCopy) {
		this.workingCopy = workingCopy;
	}

	@Override
	public void setLastDeath(List<Waypoint> lastDeath) {
		this.lastDeath = lastDeath;
		Registry.WAYPOINTS.sync(this.provider);
	}

	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}

	public Player getProvider() {
		return provider;
	}

	@Override
	public void incrementProgress() {
		progress++;
	}

	@Override
	public boolean addDeduplicatedWaypoint(ServerLevel level, BlockPos pos, Waypoint.Type type) {
		return addDeduplicatedWaypoint(getWorkingCopy(), level.dimension(), pos, type, getProvider());
	}

	public static boolean addDeduplicatedWaypoint(List<Waypoint> current, ResourceKey<Level> dimension, BlockPos pos, Waypoint.Type type, Nameable provider) {
		final var toAdd = new Waypoint(GlobalPos.of(dimension, pos), type);
		LOGGER.debug("Try adding " + toAdd + " to " + provider.getDisplayName().getString() + "'s working set:");
		if(current.size() >= 1 && doWaypointsMatch(current.get(current.size() - 1), toAdd)) {
			LOGGER.debug("Failed, multiple hits of the same portal");
			return false; // Multiple hits of same portal
		}
		if(current.size() >= 2 && doWaypointsMatch(current.get(current.size() - 2), toAdd)) {
			LOGGER.debug("Failed, two-segment-loop detected");
			return false; // Going back and forth through one portal
		}
		LOGGER.debug("Success, length of working set: " + current.size());
		return current.add(toAdd);
	}

	private static boolean doWaypointsMatch(Waypoint one, Waypoint theOther) {
		return one.equals(theOther) || (one.isWaypointWithinRangeOf(theOther.position().dimension(), theOther.position().pos(), 5) && one.type() == theOther.type());
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		progress = tag.getInt("Progress");
		lastDeath = listFromNbt(tag.getList("LastDeath", Tag.TAG_COMPOUND));
		workingCopy = listFromNbt(tag.getList("WorkingCopy", Tag.TAG_COMPOUND));
	}

	private List<Waypoint> listFromNbt(ListTag listTag) {
		List<Waypoint> result = new ArrayList<>();
		for (int i = 0; i < listTag.size(); i++) {
			var compoundTag = listTag.getCompound(i);
			GlobalPos pos = GlobalPos.of(dimensionFromNbt(compoundTag).get(), new BlockPos(NbtUtils.readBlockPos(compoundTag)));
			String id = compoundTag.getString("Type");
			result.add(new Waypoint(pos, Waypoint.Type.fromId(id)));
		}
		return result;
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		tag.putInt("Progress", progress);
		tag.put("LastDeath", listToNbt(lastDeath));
		tag.put("WorkingCopy", listToNbt(workingCopy));
	}

	private ListTag listToNbt(List<Waypoint> list) {
		ListTag listTag = new ListTag();
		for (var waypoint : list) {
			var compoundTag = new CompoundTag();
			compoundTag.putString("Type", waypoint.type().getId());
			compoundTag.merge(NbtUtils.writeBlockPos(waypoint.position().pos()));
			Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, waypoint.position().dimension()).result().ifPresent(nbt -> compoundTag.put("Dimension", nbt));
			listTag.add(compoundTag);
		}
		return listTag;
	}

	@Override
	public boolean shouldCopyForRespawn(boolean lossless, boolean keepInventory, boolean sameCharacter) {
		return sameCharacter;
	}

	@Override
	public void copyForRespawn(WaypointListComponent original, boolean lossless, boolean keepInventory, boolean sameCharacter) {
		var oldPlayer = original.getProvider();
		PlayerComponent.super.copyForRespawn(original, lossless, keepInventory, sameCharacter);
		if(lossless) {
			return;
		}
		final var oldPos = GlobalPos.of(oldPlayer.level.dimension(), new BlockPos(oldPlayer.position()));
		getWorkingCopy().add(new Waypoint(oldPos, Waypoint.Type.DEATH));
		setProgress(0);
		setLastDeath(getWorkingCopy());
		setWorkingCopy(new ArrayList<>());
	}

	private static Optional<ResourceKey<Level>> dimensionFromNbt(CompoundTag nbt) {
		return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, nbt.get("Dimension")).result();
	}

	@Override
	public boolean shouldSyncWith(ServerPlayer player) {
		return player == this.provider; // only sync with the provider itself
	}

	@Override
	public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
		if(this.lastDeath.isEmpty()) {
			buf.writeBoolean(true); // Reset!
			return;
		}
		buf.writeBoolean(false); // 1 Don't reset
		var sync = this.lastDeath.get(this.progress); // Only sync next destination
		buf.writeGlobalPos(sync.position()); // 2 destination pos
		buf.writeUtf(sync.type().getId()); // 3 destination type
		LOGGER.debug("Synced " + sync + " to " + recipient.getDisplayName().getString());
	}

	@Override
	public void applySyncPacket(FriendlyByteBuf buf) {
		if(buf.readBoolean()) { // 1 Reset?
			lastDeath.clear();
			this.progress = 0;
			return;
		}
		var globalPos = buf.readGlobalPos(); // 2 destination pos
		var type = buf.readUtf(); // 3 destination type
		var sync = new Waypoint(globalPos, Waypoint.Type.fromId(type));
		this.lastDeath.clear();
		this.lastDeath.add(0, sync); // Client only has 0 set!
		LOGGER.debug("Synced " + sync + " from server");
	}


}
