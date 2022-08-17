package de.macbrayne.quilt.recovery_plus;

import de.macbrayne.quilt.recovery_plus.components.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
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
		}
	}
}
