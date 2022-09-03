package de.macbrayne.quilt.recovery_plus.misc;

import net.minecraft.world.item.ItemStack;

public interface LocationMapUpdate {
	void markDirty(ItemStack stack);
}
