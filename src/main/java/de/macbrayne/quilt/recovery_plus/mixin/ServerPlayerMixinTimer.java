package de.macbrayne.quilt.recovery_plus.mixin;

import de.macbrayne.quilt.recovery_plus.ServerPlayerMixinTimerAccessor;
import de.macbrayne.quilt.recovery_plus.Utils;
import de.macbrayne.quilt.recovery_plus.Waypoint;
import de.macbrayne.quilt.recovery_plus.components.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.level.ServerPlayer.class)
public class ServerPlayerMixinTimer implements ServerPlayerMixinTimerAccessor {
	@Unique
	private int ticksUntilMessageSent;

	@Unique
	private Waypoint.Type messageType;

	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(CallbackInfo ci) { // Fix parameters as needed
		if (this.ticksUntilMessageSent > 0 && --this.ticksUntilMessageSent == 0) {
			final var player = (ServerPlayer) (Object) this;
			final var waypoints = Registry.WAYPOINTS.get(player);
			if(waypoints.getLastDeath().isEmpty()) {
				return;
			}

			final var message = Utils.getText(messageType);
			((ServerPlayer) (Object) this).sendSystemMessage(message, true);
			((ServerPlayer) (Object) this).sendSystemMessage(message, false); // DEBUG
		}
	}


	@Override
	public void recoveryPlus_setTimer(Waypoint.Type type, int ticksUntilMessageSent) {
		this.ticksUntilMessageSent = ticksUntilMessageSent;
		this.messageType = type;
	}
}
