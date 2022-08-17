package de.macbrayne.quilt.recovery_plus;

import de.macbrayne.quilt.recovery_plus.events.PlayerEvents;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import static net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN;

public class RecoveryPlus implements ModInitializer {
	@Override
	public void onInitialize(ModContainer mod) {
		AFTER_RESPAWN.register(PlayerEvents::afterRespawn);
	}
}
