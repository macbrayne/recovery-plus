package de.macbrayne.quilt.recovery_plus.mixin.triggers;

import de.macbrayne.quilt.recovery_plus.data.CompassTriggers;
import de.macbrayne.quilt.recovery_plus.misc.Utils;
import de.macbrayne.quilt.recovery_plus.mixin.EntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class EntityHandleNetherPortalMixin {
	@Inject(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void netherPortalEvent(CallbackInfo ci, int i, ServerLevel serverLevel, MinecraftServer minecraftServer, ResourceKey<Level> resourceKey, ServerLevel serverLevel2) {
		if((Entity) (Object) this instanceof ServerPlayer player) {
			var trigger = CompassTriggers.getTrigger(CompassTriggers.NETHER_PORTAL);
			final var entity = (EntityAccessor) this;

			BlockPos targetPos = BlockPos.containing(entity.invokeFindDimensionEntryPoint(serverLevel2).pos);
			trigger.action().accept(trigger, player, serverLevel, entity.getPortalEntrancePos(), serverLevel2, targetPos);
			Utils.doWaypointProgressionAndSync(player, serverLevel.dimension(), entity.getPortalEntrancePos());
		}
	}
}
