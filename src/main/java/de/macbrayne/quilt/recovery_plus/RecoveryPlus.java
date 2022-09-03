package de.macbrayne.quilt.recovery_plus;

import de.macbrayne.quilt.recovery_plus.events.PlayerEvents;
import de.macbrayne.quilt.recovery_plus.items.EntityCompass;
import de.macbrayne.quilt.recovery_plus.items.MagneticCompass;
import de.macbrayne.quilt.recovery_plus.items.SmartRecoveryCompass;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import static net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN;

public class RecoveryPlus implements ModInitializer {
	public static final Item SMART_RECOVERY_COMPASS = new SmartRecoveryCompass(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
	public static final Item MAGNETIC_COMPASS = new MagneticCompass(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
	public static final EntityCompass ENTITY_COMPASS = new EntityCompass(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));

	@Override
	public void onInitialize(ModContainer mod) {
		registerItem("smart_recovery_compass", SMART_RECOVERY_COMPASS);
		registerItem("entity_compass", ENTITY_COMPASS);
		registerItem("magnetic_compass", MAGNETIC_COMPASS);

		AFTER_RESPAWN.register(PlayerEvents::afterRespawn);
		ServerEntityEvents.ENTITY_LOAD.register(PlayerEvents::entityLoad);
		ServerEntityEvents.ENTITY_UNLOAD.register(PlayerEvents::entityUnload);
	}

	private void registerItem(final String id, final Item item) {
		Registry.register(Registry.ITEM, new ResourceLocation("recovery_plus", id), item);
	}
}
