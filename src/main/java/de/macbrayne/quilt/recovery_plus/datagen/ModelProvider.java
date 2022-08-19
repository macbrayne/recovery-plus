package de.macbrayne.quilt.recovery_plus.datagen;

import de.macbrayne.quilt.recovery_plus.RecoveryPlus;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.world.item.Items;

public class ModelProvider extends FabricModelProvider {
	public ModelProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {
		CompassModelGenerator compassGenerator = new CompassModelGenerator(itemModelGenerator);
		compassGenerator.generateCompassItem(RecoveryPlus.SMART_RECOVERY_COMPASS, Items.RECOVERY_COMPASS);
	}
}
