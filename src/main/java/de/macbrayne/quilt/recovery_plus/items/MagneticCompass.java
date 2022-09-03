package de.macbrayne.quilt.recovery_plus.items;

import de.macbrayne.quilt.recovery_plus.items.base.BaseCompass;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagneticCompass extends BaseCompass {
	public MagneticCompass(Properties properties) {
		super(properties);
	}

	@Override
	public MutableComponent getHoverComponent(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		return Component.translatable("item.recovery_plus.magnetic_compass.navigating");
	}


	public static GlobalPos getCompassPosition(ClientLevel clientLevel, ItemStack itemStack, Entity entity) {
		if(clientLevel == null) {
			return null;
		}
		return GlobalPos.of(clientLevel.dimension(), entity.blockPosition().north(10));
	}
}
