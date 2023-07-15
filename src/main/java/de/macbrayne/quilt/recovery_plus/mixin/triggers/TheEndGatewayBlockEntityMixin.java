package de.macbrayne.quilt.recovery_plus.mixin.triggers;

import de.macbrayne.quilt.recovery_plus.data.CompassTriggers;
import de.macbrayne.quilt.recovery_plus.misc.Utils;
import de.macbrayne.quilt.recovery_plus.mixin.TheEndGatewayBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TheEndGatewayBlockEntity.class)
public class TheEndGatewayBlockEntityMixin {
	@Inject(method = "teleportEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportToWithTicket(DDD)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void endGatewayEvent(Level world, BlockPos pos, BlockState state, Entity entity, TheEndGatewayBlockEntity blockEntity, CallbackInfo ci, BlockPos blockPos, Entity entity3) {
		if(entity3 instanceof ServerPlayer player) {
			var trigger = CompassTriggers.getTrigger(CompassTriggers.END_GATEWAY);
			var accessor = (TheEndGatewayBlockEntityAccessor) blockEntity;
			trigger.action().accept(trigger, player, (ServerLevel) world, pos, (ServerLevel) world, accessor.getExitPortal());
			Utils.doWaypointProgressionAndSync(player, world.dimension(), pos);
		}
	}
}
