package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.data.CompassTrigger;
import de.macbrayne.quilt.recovery_plus.data.CompassTriggers;
import de.macbrayne.quilt.recovery_plus.misc.Deduplication;
import de.macbrayne.quilt.recovery_plus.misc.Waypoint;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
	public boolean addDeduplicatedWaypoint(ServerLevel level, BlockPos pos, ServerLevel target, BlockPos targetPos, CompassTrigger type) {
		if(target == null || targetPos == null) {
			return Deduplication.addDeduplicatedWaypoint(getWorkingCopy(), level.dimension(), pos, null, null, type, getProvider());
		}
		return Deduplication.addDeduplicatedWaypoint(getWorkingCopy(), level.dimension(), pos, target.dimension(), targetPos, type, getProvider());
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
			GlobalPos pos = decodeGlobalPos(compoundTag);
			GlobalPos destination = decodeGlobalPos(compoundTag.getCompound("Target"));
			var id = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, compoundTag.get("Type")).result().get();
			String translation = compoundTag.contains("Translation") ? compoundTag.getString("Translation") : "";
			result.add(new Waypoint(pos, destination, CompassTriggers.getTrigger(id), translation));
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
			ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, waypoint.type().location()).result().ifPresent(nbt -> compoundTag.put("Type", nbt));

			mergeGlobalPos(compoundTag, waypoint.position());

			var destinationTag = new CompoundTag();
			mergeGlobalPos(destinationTag, waypoint.target());
			if(!destinationTag.isEmpty()) {
				compoundTag.put("Target", destinationTag);
			}
			compoundTag.putString("Translation", waypoint.translation());
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
		var action = CompassTriggers.getTrigger(CompassTriggers.DEATH).action();
		action.accept(null, (ServerPlayer) this.provider, (ServerLevel) oldPlayer.level, new BlockPos(oldPlayer.position()), null, null);
	}

	private static void mergeGlobalPos(CompoundTag tag, GlobalPos pos) {
		if(pos != null && pos.dimension() != null && pos.pos() != null) {
			tag.merge(NbtUtils.writeBlockPos(pos.pos()));
			Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, pos.dimension()).result().ifPresent(nbt -> tag.put("Dimension", nbt));
		}
	}

	private static GlobalPos decodeGlobalPos(CompoundTag source) {
		var dimension = dimensionFromNbt(source);
		if(dimension.isEmpty()) {
			return null;
		}
		return GlobalPos.of(dimension.get(), new BlockPos(NbtUtils.readBlockPos(source)));
	}

	private static Optional<ResourceKey<Level>> dimensionFromNbt(CompoundTag nbt) {
		return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, nbt.get("Dimension")).result();
	}

	private static Optional<ResourceKey<Level>> targetDimensionFromNbt(CompoundTag nbt) {
		return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, nbt.get("TargetDimension")).result();
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
		buf.writeUtf(sync.translation()); // 3 translation
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
		var type = buf.readUtf(); // 3 translation
		var sync = new Waypoint(globalPos, null, null, type);
		this.lastDeath.clear();
		this.lastDeath.add(0, sync); // Client only has 0 set!
		LOGGER.debug("Synced " + sync + " from server");
	}
}
