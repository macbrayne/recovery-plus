package de.macbrayne.quilt.recovery_plus;

import de.macbrayne.quilt.recovery_plus.data.CompassTriggers;
import de.macbrayne.quilt.recovery_plus.items.SmartRecoveryCompass;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecoveryPlus implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("recovery_plus");
	public static final Item SMART_RECOVERY_COMPASS = new SmartRecoveryCompass(new Item.Properties());

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("recovery_plus", "smart_recovery_compass"), SMART_RECOVERY_COMPASS);
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.addAfter(Items.RECOVERY_COMPASS, SMART_RECOVERY_COMPASS));
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new CompassTriggers());
		LOGGER.debug("RecoveryPlus ModInit: Done");
	}
}
