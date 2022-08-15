package de.macbrayne.quilt.recovery_plus;

import de.macbrayne.quilt.recovery_plus.components.PositionListComponent;
import de.macbrayne.quilt.recovery_plus.events.PlayerEvents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN;

public class RecoveryPlus implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod name as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Example Mod");

	@Override
	public void onInitialize(ModContainer mod) {

		LOGGER.info("Hello Quilt world from {}!", mod.metadata().name());

		AFTER_RESPAWN.register(PlayerEvents::afterRespawn);
	}
}
