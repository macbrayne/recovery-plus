package de.macbrayne.quilt.recovery_plus.items;

import de.macbrayne.quilt.recovery_plus.components.Registry;
import de.macbrayne.quilt.recovery_plus.items.base.BaseCompass;
import de.macbrayne.quilt.recovery_plus.misc.Utils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmartRecoveryCompass extends BaseCompass {
	@Nullable
	private String descriptionId;

	public SmartRecoveryCompass(Properties properties) {
		super(properties);
	}

	@Override
	public MutableComponent getHoverComponent(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
		Player player = Minecraft.getInstance().player;
		return Utils.getText(player);
	}

	@Override
	protected String getOrCreateDescriptionId() {
		if (this.descriptionId == null) {
			this.descriptionId = Util.makeDescriptionId("item", net.minecraft.core.Registry.ITEM.getKey(Items.RECOVERY_COMPASS));
		}

		return this.descriptionId;
	}


	public static GlobalPos getCompassPosition(ClientLevel clientLevel, ItemStack itemStack, Entity entity) {
		if(!(entity instanceof Player)) {
			return null;
		}
		final var waypoints = Registry.WAYPOINTS.get(entity);
		if(waypoints.getLastDeath().isEmpty()) {
			return null;
		}
		final var waypoint = waypoints.getLastDeath().get(0);
		return waypoint.position();
	}
}
