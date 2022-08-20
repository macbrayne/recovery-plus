package de.macbrayne.quilt.recovery_plus.items;

import de.macbrayne.quilt.recovery_plus.components.Registry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PropertyFunctions {
	public static GlobalPos getRecoveryCompassPosition(ClientLevel clientLevel, ItemStack itemStack, Entity entity) {
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
