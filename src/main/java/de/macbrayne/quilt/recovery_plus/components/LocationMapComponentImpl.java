package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.misc.LocationMapUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LocationMapComponentImpl implements LocationMapComponent {
	private final Map<ItemStack, LocationMapUpdate> listeners = new WeakHashMap<>();
	private Map<UUID, GlobalPos> trackedEntities = new HashMap<>();

	public LocationMapComponentImpl(Scoreboard scoreboard, @Nullable MinecraftServer server) {
	}

	@Override
	public void put(UUID uuid, GlobalPos pos) {
		trackedEntities.put(uuid, pos);
		markDirty();
	}

	@Override
	public Optional<GlobalPos> remove(UUID uuid) {
		var result = trackedEntities.remove(uuid);
		markDirty();
		return Optional.ofNullable(result);
	}

	@Override
	public Optional<GlobalPos> get(UUID uuid) {
		return Optional.ofNullable(trackedEntities.get(uuid));
	}

	@Override
	public boolean contains(UUID uuid) {
		return trackedEntities.containsKey(uuid);
	}

	@Override
	public void registerUpdateListener(LocationMapUpdate listener, ItemStack stack) {
		listeners.put(stack, listener);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		trackedEntities = mapFromNbt(tag.getList("Entities", Tag.TAG_COMPOUND));
	}

	private Map<UUID, GlobalPos> mapFromNbt(ListTag listTag) {
		System.out.println(listTag);
		HashMap<UUID, GlobalPos> result = new HashMap<>();
		for (int i = 0; i < listTag.size(); i++) {
			var compoundTag = listTag.getCompound(i);
			GlobalPos pos = null;
			if(compoundTag.contains("Dimension")) {
				pos = GlobalPos.of(dimensionFromNbt(compoundTag).get(), new BlockPos(NbtUtils.readBlockPos(compoundTag)));
			}
			UUID uuid = compoundTag.getUUID("UUID");
			result.put(uuid, pos);
		}
		markDirty();
		return result;
	}

	private static Optional<ResourceKey<Level>> dimensionFromNbt(CompoundTag nbt) {
		return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, nbt.get("Dimension")).result();
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		tag.put("Entities", mapToNbt(trackedEntities));
	}

	private ListTag mapToNbt(Map<UUID, GlobalPos> map) {
		ListTag listTag = new ListTag();
		for (var entry : map.entrySet()) {
			var compoundTag = new CompoundTag();
			compoundTag.putUUID("UUID", entry.getKey());
			if(entry.getValue() != null) {
				compoundTag.merge(NbtUtils.writeBlockPos(entry.getValue().pos()));
				Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue().dimension()).result().ifPresent(nbt -> compoundTag.put("Dimension", nbt));
			}
			listTag.add(compoundTag);
		}
		return listTag;
	}

	private void markDirty() {
		for(var entry : listeners.entrySet()) {
			entry.getValue().markDirty(entry.getKey());
		}
	}
}
