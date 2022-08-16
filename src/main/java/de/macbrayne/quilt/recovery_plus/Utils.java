package de.macbrayne.quilt.recovery_plus;

import de.macbrayne.quilt.recovery_plus.components.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class Utils {
	public static void doWaypointProgressionAndSync(ServerPlayer player, Level world, BlockPos pos) {
		final var waypoints = Registration.WAYPOINTS.get(player);
		final var currentWaypoint = waypoints.getLastDeath().get(waypoints.getProgress());
		if (currentWaypoint.position().dimension() == world.dimension() && currentWaypoint.position().pos().closerThan(pos, 5)) {
			waypoints.incrementProgress();
			Registration.WAYPOINTS.sync(player);
		}
	}
}
