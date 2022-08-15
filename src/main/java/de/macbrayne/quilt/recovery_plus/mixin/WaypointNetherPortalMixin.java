package de.macbrayne.quilt.recovery_plus.mixin;

import de.macbrayne.quilt.recovery_plus.Waypoint;
import de.macbrayne.quilt.recovery_plus.components.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.entity.Entity.class)
public abstract class WaypointNetherPortalMixin {

	@Unique
	private static final Logger LOGGER = LoggerFactory.getLogger("recovery_plus");

	@Shadow
	public abstract BlockPos getOnPos();


	@Shadow
	protected abstract BlockPos getOnPos(float offset);

	@Inject(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;"), cancellable = false)
	private void netherPortalEvent(CallbackInfo ci) {
		ServerLevel serverLevel = (ServerLevel) ((EntityAccessor) this).getLevel();
		LOGGER.info("YAY?");

		if((Entity) (Object) this instanceof Player player) {
			Registration.WAYPOINTS.get(player).getWorkingCopy().add(new Waypoint(GlobalPos.of(serverLevel.dimension(), getOnPos()), Waypoint.Type.NETHER_PORTAL));
			LOGGER.info("YAY!");
			LOGGER.error(Registration.WAYPOINTS.get(player).getWorkingCopy().toString());
		}
	}
}
