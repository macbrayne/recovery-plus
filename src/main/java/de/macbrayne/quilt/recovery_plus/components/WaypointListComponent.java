package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.Waypoint;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WaypointListComponent implements PositionListComponent, AutoSyncedComponent, PlayerComponent<PositionListComponent> {
	private final Object provider;
	int progress = 0;
	private List<Waypoint> lastDeath = new ArrayList<>();
	private List<Waypoint> workingCopy = new ArrayList<>(); // Server Only

	public WaypointListComponent(Object provider) {
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
		Registration.WAYPOINTS.sync(this.provider);
	}

	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}

	public Object getProvider() {
		return provider;
	}

	@Override
	public void incrementProgress() {
		progress++;
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
			buf.writeBoolean(true); // Reset list!
			return;
		}
		buf.writeBoolean(false); // 1 Don't reset list

		var sync = this.lastDeath.get(this.progress); // Only sync next destination
		// buf.writeVarInt(progress); // 2 progress
		buf.writeGlobalPos(sync.position()); // 3 destination pos
		buf.writeUtf(sync.type().getId()); // 4 destination type
	}

	@Override
	public void applySyncPacket(FriendlyByteBuf buf) {
		if(buf.readBoolean()) { // 1 clear?
			lastDeath.clear();
			this.progress = 0;
			return;
		}
		// progress = buf.readVarInt(); // 2 progress
		var globalPos = buf.readGlobalPos(); // 3 destination pos
		var type = buf.readUtf(); // 4 destination type
		var sync = new Waypoint(globalPos, Waypoint.Type.fromId(type));
		this.lastDeath.clear();
		this.lastDeath.add(0, sync); // Client only has 0 set!
	}
}
