package de.macbrayne.quilt.recovery_plus.datagen;

import de.macbrayne.quilt.recovery_plus.mixin.ItemModelGeneratorsAccessor;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.world.item.Item;

import java.util.Locale;

public class CompassModelGenerator {
	final ItemModelGeneratorsAccessor accessor;

	public CompassModelGenerator(ItemModelGenerators itemModelGenerators) {
		this.accessor = (ItemModelGeneratorsAccessor) itemModelGenerators;
	}

	public final void generateCompassItem(Item item, Item texture) {
		for(int i = 0; i < 32; ++i) {
			if (i != 16) {
				generateFlatItem(item, texture, String.format(Locale.ROOT, "_%02d", i), ModelTemplates.FLAT_ITEM);
			}
		}

	}

	public final void generateFlatItem(Item item, Item texture, String suffix, ModelTemplate model) {
		model.create(ModelLocationUtils.getModelLocation(item, suffix), TextureMapping.layer0(TextureMapping.getItemTexture(texture, suffix)), accessor.getOutput());
	}
}
