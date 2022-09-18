package de.macbrayne.quilt.recovery_plus.misc;

import de.macbrayne.quilt.recovery_plus.components.WaypointListComponentImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Deduplication {
	public static final Logger LOGGER = LoggerFactory.getLogger("recovery_plus");
	public static boolean addDeduplicatedWaypoint(List<Waypoint> current, ResourceKey<Level> dimension, BlockPos pos, Waypoint.Type type, Nameable provider) {
		final var toAdd = new Waypoint(GlobalPos.of(dimension, pos), type);
		LOGGER.debug("Try adding " + toAdd + " to " + provider.getDisplayName().getString() + "'s working set:");
		if(current.size() >= 1 && doWaypointsMatch(current.get(current.size() - 1), toAdd)) {
			LOGGER.debug("Failed, multiple hits of the same portal");
			return false; // Multiple hits of same portal
		}
		if(current.size() >= 2 && doWaypointsMatch(current.get(current.size() - 2), toAdd)) {
			LOGGER.debug("Failed, two-segment-loop detected");
			return false; // Going back and forth through one portal
		}
		LOGGER.debug("Success, length of working set: " + current.size());
		return current.add(toAdd);
	}

	public static boolean doWaypointsMatch(Waypoint one, Waypoint theOther) {
		return one.equals(theOther) || (one.isWaypointWithinRangeOf(theOther.position().dimension(), theOther.position().pos(), 5) && one.type() == theOther.type());
	}
}
