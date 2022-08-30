package de.macbrayne.quilt.recovery_plus.items;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static de.macbrayne.quilt.recovery_plus.items.PropertyFunctions.getMagneticCompassPosition;

public class MagneticCompass extends Item {
	public MagneticCompass(Properties properties) {
		super(properties);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		Player player = Minecraft.getInstance().player;
		var text = Component.translatable("item.recovery_plus.magnetic_compass.navigating", getMagneticCompassPosition((ClientLevel) world, stack, player));
		tooltip.add(text.withStyle(style -> style.applyFormats(ChatFormatting.ITALIC, ChatFormatting.DARK_AQUA)));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
}
