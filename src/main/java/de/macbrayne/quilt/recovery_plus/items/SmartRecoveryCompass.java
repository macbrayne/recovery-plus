package de.macbrayne.quilt.recovery_plus.items;

import de.macbrayne.quilt.recovery_plus.components.Registry;
import de.macbrayne.quilt.recovery_plus.misc.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmartRecoveryCompass extends Item {
	@Nullable
	private String descriptionId;

	public SmartRecoveryCompass(Properties properties) {
		super(properties);
	}

	@Override
	@Environment(EnvType.CLIENT)
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

	@Override
	protected String getOrCreateDescriptionId() {
		if (this.descriptionId == null) {
			this.descriptionId = Util.makeDescriptionId("item", net.minecraft.core.Registry.ITEM.getKey(Items.RECOVERY_COMPASS));
		}

		return this.descriptionId;
	}
}
