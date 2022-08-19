package de.macbrayne.quilt.recovery_plus.misc;

import de.macbrayne.quilt.recovery_plus.components.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Utils {
	public static void doWaypointProgressionAndSync(ServerPlayer player, ResourceKey<Level> dimension, BlockPos pos) {
		final var waypoints = Registry.WAYPOINTS.get(player);
		if(waypoints.getLastDeath().isEmpty()) {
			return;
		}

		final var currentWaypoint = waypoints.getLastDeath().get(waypoints.getProgress());
		if (currentWaypoint.isWaypointWithinRangeOf(dimension, pos, 5)) {
			waypoints.incrementProgress();
			Registry.WAYPOINTS.sync(player);
			announceProgress(player, currentWaypoint.type());
		}
	}

	public static void announceProgress(ServerPlayer player, Waypoint.Type type) {
		((ServerPlayerMixinTimerAccessor) player).recoveryPlus_setTimer(type, 100);
	}

	public static MutableComponent getText(Player player) {
		final var waypoints = Registry.WAYPOINTS.get(player);
		var type = waypoints.getLastDeath().get(waypoints.getLastDeath().size() - 1).type();
		return getText(type);
	}

	public static MutableComponent getText(Waypoint.Type type) {
		var destination = Component.translatable("item.recovery_plus.recovery_compass.type." + type.getId());
		return Component.translatable("item.recovery_plus.recovery_compass.navigating", destination);
	}
}
