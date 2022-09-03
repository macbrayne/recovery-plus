package de.macbrayne.quilt.recovery_plus.items.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseCompass extends Item {
	public BaseCompass(Properties properties) {
		super(properties);
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}


	@NotNull
	public abstract MutableComponent getHoverComponent(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context);


	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		Player player = Minecraft.getInstance().player;
		tooltip.add(getHoverComponent(stack, world, tooltip, context).withStyle(style -> style.applyFormats(ChatFormatting.ITALIC, ChatFormatting.DARK_AQUA)));
	}
}
