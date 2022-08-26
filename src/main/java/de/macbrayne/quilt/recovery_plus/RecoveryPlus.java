package de.macbrayne.quilt.recovery_plus;

import de.macbrayne.quilt.recovery_plus.events.PlayerEvents;
import de.macbrayne.quilt.recovery_plus.items.SmartRecoveryCompass;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import static net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN;

public class RecoveryPlus implements ModInitializer {
	public static final Item SMART_RECOVERY_COMPASS = new SmartRecoveryCompass(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));

	@Override
	public void onInitialize(ModContainer mod) {
		Registry.register(Registry.ITEM, new ResourceLocation("recovery_plus", "smart_recovery_compass"), SMART_RECOVERY_COMPASS);
		AFTER_RESPAWN.register(PlayerEvents::afterRespawn);
	}

}
