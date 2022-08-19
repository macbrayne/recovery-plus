package de.macbrayne.quilt.recovery_plus;

import de.macbrayne.quilt.recovery_plus.events.PlayerEvents;
import de.macbrayne.quilt.recovery_plus.items.PropertyFunctions;
import de.macbrayne.quilt.recovery_plus.items.SmartRecoveryCompass;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.tooltip.api.client.ItemTooltipCallback;

import static net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN;

public class RecoveryPlus implements ModInitializer {
	public static final Item SMART_RECOVERY_COMPASS = new SmartRecoveryCompass(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));

	@Override
	public void onInitialize(ModContainer mod) {
		final var smartRecoveryCompassLocation = locationOf("smart_recovery_compass");
		Registry.register(Registry.ITEM, smartRecoveryCompassLocation, SMART_RECOVERY_COMPASS);
		ItemProperties.register(SMART_RECOVERY_COMPASS, new ResourceLocation("angle"), new CompassItemPropertyFunction(PropertyFunctions::getPropertyFunction));
		AFTER_RESPAWN.register(PlayerEvents::afterRespawn);
	}

	ResourceLocation locationOf(String id) {
		return new ResourceLocation("recovery_plus", id);
	}
}
