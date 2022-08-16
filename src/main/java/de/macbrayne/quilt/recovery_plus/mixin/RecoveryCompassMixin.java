package de.macbrayne.quilt.recovery_plus.mixin;

import de.macbrayne.quilt.recovery_plus.components.Registration;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.renderer.item.ItemProperties.class)
public class RecoveryCompassMixin {
	@Inject(method = "m_smilstgj", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
	private static void redirectTarget(ClientLevel world, ItemStack stack, Entity entity, CallbackInfoReturnable<GlobalPos> cir) {
		final var WAYPOINTS = Registration.WAYPOINTS.get(entity);
		System.out.println(WAYPOINTS.getLastDeath());
		if(WAYPOINTS.getLastDeath().isEmpty()) {
			cir.setReturnValue(null);
			return;
		}
		final var WAYPOINT = WAYPOINTS.getLastDeath().get(0);
		cir.setReturnValue(WAYPOINT.position());
	}
}
