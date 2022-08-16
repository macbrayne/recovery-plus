package de.macbrayne.quilt.recovery_plus.mixin.triggers;

import de.macbrayne.quilt.recovery_plus.Utils;
import de.macbrayne.quilt.recovery_plus.Waypoint;
import de.macbrayne.quilt.recovery_plus.components.Registration;
import de.macbrayne.quilt.recovery_plus.mixin.EntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class EntityHandleNetherPortalMixin {
	@Unique
	private static final Logger LOGGER = LoggerFactory.getLogger("recovery_plus");

	@Shadow
	public abstract BlockPos getOnPos();


	@Shadow
	protected abstract BlockPos getOnPos(float offset);

	@Inject(method = "handleInsidePortal", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;isInsidePortal:Z"))
	private void insidePortal(BlockPos pos, CallbackInfo ci) {
		if((Entity) (Object) this instanceof ServerPlayer player) {
			ServerLevel serverLevel = (ServerLevel) ((EntityAccessor) this).getLevel();
			final var waypoints = Registration.WAYPOINTS.get(player);

		}
	}

	@Inject(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;"), cancellable = false)
	private void netherPortalEvent(CallbackInfo ci) {
		final var serverLevel = (ServerLevel) ((EntityAccessor) this).getLevel();
		if((Entity) (Object) this instanceof ServerPlayer player) {
			final var entity = (EntityAccessor) (Object) this;
			final var waypoints = Registration.WAYPOINTS.get(player);
			waypoints.getWorkingCopy().add(new Waypoint(GlobalPos.of(serverLevel.dimension(), getOnPos()), Waypoint.Type.NETHER_PORTAL));

			Utils.doWaypointProgressionAndSync(player, serverLevel, entity.getPortalEntrancePos());
		}
	}
}
