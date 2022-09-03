package de.macbrayne.quilt.recovery_plus.client;

import de.macbrayne.quilt.recovery_plus.RecoveryPlus;
import de.macbrayne.quilt.recovery_plus.items.EntityCompass;
import de.macbrayne.quilt.recovery_plus.items.MagneticCompass;
import de.macbrayne.quilt.recovery_plus.items.SmartRecoveryCompass;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class RecoveryPlusClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		ItemProperties.register(RecoveryPlus.SMART_RECOVERY_COMPASS, new ResourceLocation("angle"), new CompassItemPropertyFunction(SmartRecoveryCompass::getCompassPosition));
		ItemProperties.register(RecoveryPlus.MAGNETIC_COMPASS, new ResourceLocation("angle"), new CompassItemPropertyFunction(MagneticCompass::getCompassPosition));
		ItemProperties.register(RecoveryPlus.ENTITY_COMPASS, new ResourceLocation("angle"), new CompassItemPropertyFunction(EntityCompass::getCompassPosition));
	}
}
