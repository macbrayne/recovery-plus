package de.macbrayne.quilt.recovery_plus.items;

import de.macbrayne.quilt.recovery_plus.components.Registry;
import de.macbrayne.quilt.recovery_plus.misc.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmartRecoveryCompass extends Item {
	public SmartRecoveryCompass(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		super.appendHoverText(stack, world, tooltip, context);
		Player player = Minecraft.getInstance().player;
		final var waypoints = Registry.WAYPOINTS.get(player);
		if(waypoints.getLastDeath().isEmpty()) {
			return;
		}
		tooltip.add(Utils.getText(player).withStyle(style -> style.applyFormats(ChatFormatting.ITALIC, ChatFormatting.DARK_AQUA)));
	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
}
