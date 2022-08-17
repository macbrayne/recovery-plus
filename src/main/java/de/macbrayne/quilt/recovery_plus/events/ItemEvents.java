package de.macbrayne.quilt.recovery_plus.events;

import de.macbrayne.quilt.recovery_plus.Utils;
import de.macbrayne.quilt.recovery_plus.components.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemEvents {
	public static void tooltip(ItemStack itemStack, @Nullable Player player, TooltipFlag tooltipFlag, List<Component> components) {
		if(itemStack.is(Items.RECOVERY_COMPASS)) {
			if(player == null) {
				return;
			}
			final var waypoints = Registry.WAYPOINTS.get(player);
			if(waypoints.getLastDeath().isEmpty()) {
				return;
			}
			components.add(Utils.getText(player).withStyle(style -> style.applyFormats(ChatFormatting.ITALIC, ChatFormatting.DARK_AQUA)));
		}
	}
}
