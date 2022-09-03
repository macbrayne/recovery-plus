package de.macbrayne.quilt.recovery_plus.items;

import de.macbrayne.quilt.recovery_plus.RecoveryPlus;
import de.macbrayne.quilt.recovery_plus.components.Registry;
import de.macbrayne.quilt.recovery_plus.items.base.BaseCompass;
import de.macbrayne.quilt.recovery_plus.misc.LocationMapUpdate;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EntityCompass extends BaseCompass implements LocationMapUpdate {
	public EntityCompass(Properties properties) {
		super(properties);
	}

	@Override
	public MutableComponent getHoverComponent(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag contex) {
		if(NbtHelpers.getName(stack) == null) return Component.empty();
		return Component.translatable("item.recovery_plus.entity_compass.navigating", NbtHelpers.getName(stack));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (!world.isClientSide) {
			if(NbtHelpers.getLocationMapDirty(stack)) {
				var trackedEntities = Registry.TRACKED_ENTITIES.get(world.getScoreboard());
				Entity entityTarget = NbtHelpers.getEntityTarget(stack, world);
				trackedEntities.registerUpdateListener(this, stack);
				if(entityTarget == null) {
					return;
				}
				if(trackedEntities.contains(entityTarget.getUUID()) && trackedEntities.get(entityTarget.getUUID()).isPresent()) {
					NbtHelpers.setGlobalPosTarget(stack, trackedEntities.get(entityTarget.getUUID()).get());
				}
				NbtHelpers.setLocationMapDirty(stack, false);
			}
		}
	}

	@Override
	public void markDirty(ItemStack stack) {
		NbtHelpers.setLocationMapDirty(stack, true);
	}


	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
		if(!user.level.isClientSide) {
			if(stack.is(RecoveryPlus.ENTITY_COMPASS)) {
				var trackedEntities = Registry.TRACKED_ENTITIES.get(user.level.getScoreboard());
				if(trackedEntities.contains(entity.getUUID())) {
					return InteractionResult.FAIL;
				}
				if(NbtHelpers.getEntityTarget(stack, user.level) != null) {
					trackedEntities.remove(NbtHelpers.getEntityTarget(stack, user.level).getUUID());
				}
				trackedEntities.put(entity.getUUID(), null);
				NbtHelpers.setEntityTarget(stack, entity);
				NbtHelpers.setName(stack, entity.getName());
			}
		}
		return InteractionResult.SUCCESS;
	}

	private static Optional<ResourceKey<Level>> getDimension(CompoundTag nbt) {
		return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, nbt.get("Dimension")).result();
	}


	public static GlobalPos getCompassPosition(ClientLevel clientLevel, ItemStack itemStack, Entity entity) {
		Entity entityTarget = NbtHelpers.getEntityTarget(itemStack, clientLevel);
		GlobalPos globalPosTarget = NbtHelpers.getGlobalPosTarget(itemStack);
		if(entityTarget == null && globalPosTarget == null) {
			return null;
		}
		if(globalPosTarget != null) {
			return globalPosTarget;
		}
		return GlobalPos.of(entityTarget.level.dimension(), entityTarget.blockPosition());
	}

	static class NbtHelpers {
		static Component getName(ItemStack stack) {
			return Component.Serializer.fromJson(getTag(stack).getString("Name"));
		}

		static void setName(ItemStack stack, Component name) {
			getTag(stack).putString("Name", Component.Serializer.toJson(name));
		}

		static Entity getEntityTarget(ItemStack stack, Level world) {
			return world.getEntity(getTag(stack).getInt("NetworkId"));
		}

		static void setEntityTarget(ItemStack stack, Entity entity) {
			getTag(stack).putInt("NetworkId", entity.getId());
		}

		static GlobalPos getGlobalPosTarget(ItemStack stack) {
			if(!getDimension(getTag(stack)).isPresent()) {
				return null;
			}
			return GlobalPos.of(getDimension(getTag(stack)).get(), new BlockPos(NbtUtils.readBlockPos(getTag(stack))));
		}

		static void setGlobalPosTarget(ItemStack stack, GlobalPos globalPos) {
			getTag(stack).merge(NbtUtils.writeBlockPos(globalPos.pos()));
			Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, globalPos.dimension()).result().ifPresent(nbt -> getTag(stack).put("Dimension", nbt));
		}

		static boolean getLocationMapDirty(ItemStack stack) {
			return !getTag(stack).getBoolean("LocationMapClean");
		}

		static void setLocationMapDirty(ItemStack stack, boolean state) {
			getTag(stack).putBoolean("LocationMapClean", !state);
		}

		private static CompoundTag getTag(ItemStack stack) {
			return stack.getTagElement("EntityCompass");
		}
	}
}
