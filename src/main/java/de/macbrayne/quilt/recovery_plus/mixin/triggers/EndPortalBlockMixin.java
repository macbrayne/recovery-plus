package de.macbrayne.quilt.recovery_plus.mixin.triggers;

import de.macbrayne.quilt.recovery_plus.misc.Utils;
import de.macbrayne.quilt.recovery_plus.misc.Waypoint;
import de.macbrayne.quilt.recovery_plus.components.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
	@Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;"))
	void endPortalEvent(BlockState state, Level world, BlockPos pos, Entity entity, CallbackInfo ci) {
		if(entity instanceof ServerPlayer player) {
			Registry.WAYPOINTS.get(player).addDeduplicatedWaypoint(world.dimension(), pos, Waypoint.Type.END_PORTAL);

			Utils.doWaypointProgressionAndSync(player, world.dimension(), pos);
		}
	}
}
