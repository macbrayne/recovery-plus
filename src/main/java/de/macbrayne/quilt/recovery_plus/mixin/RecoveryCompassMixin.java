package de.macbrayne.quilt.recovery_plus.mixin;

import de.macbrayne.quilt.recovery_plus.components.Registration;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.renderer.item.ItemProperties.class)
public class RecoveryCompassMixin {
	@Unique
	@Inject(method = "m_smilstgj", at = @At("RETURN"))
	private static void redirectTarget(ClientLevel world, ItemStack stack, Entity entity, CallbackInfoReturnable<GlobalPos> cir) {
		final var WAYPOINTS = Registration.WAYPOINTS.get(entity);
		final var WAYPOINT = WAYPOINTS.getLastDeath().get(WAYPOINTS.getProgress());
		cir.setReturnValue(WAYPOINT.position());
	}
}
