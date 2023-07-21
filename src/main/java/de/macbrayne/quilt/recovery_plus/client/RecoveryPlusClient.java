package de.macbrayne.quilt.recovery_plus.client;

import de.macbrayne.quilt.recovery_plus.RecoveryPlus;
import de.macbrayne.quilt.recovery_plus.items.SmartRecoveryCompass;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class RecoveryPlusClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemProperties.register(RecoveryPlus.SMART_RECOVERY_COMPASS, new ResourceLocation("angle"), new CompassItemPropertyFunction(SmartRecoveryCompass::getRecoveryCompassPosition));
	}
}
