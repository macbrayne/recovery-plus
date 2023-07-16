package de.macbrayne.quilt.recovery_plus.misc;

import de.macbrayne.quilt.recovery_plus.components.Registry;
import de.macbrayne.quilt.recovery_plus.data.CompassTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	public static final Logger LOGGER = LoggerFactory.getLogger("recovery_plus");
	public static void doWaypointProgressionAndSync(ServerPlayer player, ResourceKey<Level> dimension, BlockPos pos) {
		final var waypoints = Registry.WAYPOINTS.get(player);
		if(waypoints.getLastDeath().isEmpty()) {
			return;
		}

		final var currentWaypoint = waypoints.getLastDeath().get(waypoints.getProgress());
		if (currentWaypoint.isWaypointWithinRangeOf(dimension, pos, 5) && waypoints.getLastDeath().size() > waypoints.getProgress() + 1) {
			waypoints.incrementProgress();
			Registry.WAYPOINTS.sync(player);
			final var newWaypoint = waypoints.getLastDeath().get(waypoints.getProgress());
			announceProgress(player, newWaypoint.type());
		}
		LOGGER.debug("Synced waypoint at " + pos.toShortString() + " located in " + dimension.toString() + " to " + player.getDisplayName().getString());

	}

	public static void announceProgress(ServerPlayer player, CompassTrigger type) {
		((ServerPlayerMixinTimerAccessor) player).recoveryPlus_setTimer(type, 60);
	}

	public static MutableComponent getText(CompassTrigger type) {
		return getText(type.location().toString());
	}

	public static MutableComponent getText(String translation) {
		var destination = Component.translatable("item.recovery_plus.recovery_compass.type." + translation);
		return Component.translatable("item.recovery_plus.recovery_compass.navigating", destination);
	}
}
