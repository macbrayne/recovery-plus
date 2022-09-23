package de.macbrayne.quilt.recovery_plus;

import de.macbrayne.quilt.recovery_plus.blocks.ModfestUtil;
import de.macbrayne.quilt.recovery_plus.data.CompassTriggers;
import de.macbrayne.quilt.recovery_plus.items.SmartRecoveryCompass;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.quiltmc.qsl.resource.loader.api.ResourceLoaderEvents;

public class RecoveryPlus implements ModInitializer {
	public static final Item SMART_RECOVERY_COMPASS = new SmartRecoveryCompass(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
	public static final Block MODFEST_UTIL = new ModfestUtil(QuiltBlockSettings.of(Material.STONE, MaterialColor.SAND).noCollission().strength(4.0f));

	@Override
	public void onInitialize(ModContainer mod) {
		Registry.register(Registry.ITEM, new ResourceLocation("recovery_plus", "smart_recovery_compass"), SMART_RECOVERY_COMPASS);
		Registry.register(Registry.BLOCK, new ResourceLocation("recovery_plus", "clear_working_set"), MODFEST_UTIL);
		Registry.register(Registry.ITEM, new ResourceLocation("recovery_plus", "clear_working_set"), new BlockItem(MODFEST_UTIL, new QuiltItemSettings()));
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new CompassTriggers());
	}
}
