package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.Waypoint;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WaypointListComponent implements PositionListComponent, AutoSyncedComponent, PlayerComponent<PositionListComponent> {
	private final Object provider;
	int progress = 0;
	private List<Waypoint> lastDeath = new ArrayList<>();
	private List<Waypoint> workingCopy = new ArrayList<>();

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
		Registration.WAYPOINTS.sync(this.provider);
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

	@Override
	public void readFromNbt(CompoundTag tag) {
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
}
