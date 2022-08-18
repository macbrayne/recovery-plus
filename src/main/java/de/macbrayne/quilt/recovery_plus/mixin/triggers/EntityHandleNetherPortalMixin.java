package de.macbrayne.quilt.recovery_plus.mixin.triggers;

import de.macbrayne.quilt.recovery_plus.misc.Utils;
import de.macbrayne.quilt.recovery_plus.misc.Waypoint;
import de.macbrayne.quilt.recovery_plus.components.Registry;
import de.macbrayne.quilt.recovery_plus.mixin.EntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class EntityHandleNetherPortalMixin {
	@Shadow
	public abstract BlockPos getOnPos();

	@Inject(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;"))
	private void netherPortalEvent(CallbackInfo ci) {
		if((Entity) (Object) this instanceof ServerPlayer player) {
			final var serverLevel = (ServerLevel) ((EntityAccessor) this).getLevel();
			ResourceKey<Level> destination = serverLevel.dimension() == Level.NETHER ? Level.OVERWORLD : Level.NETHER;
			final var entity = (EntityAccessor) this;
			final var waypoints = Registry.WAYPOINTS.get(player);
			waypoints.addDeduplicatedWaypoint(player, serverLevel, destination, getOnPos(), Waypoint.Type.NETHER_PORTAL);

			Utils.doWaypointProgressionAndSync(player, serverLevel.dimension(), entity.getPortalEntrancePos());
		}
	}
}
