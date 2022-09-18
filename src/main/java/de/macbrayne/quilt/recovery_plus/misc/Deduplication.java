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
			current.remove(current.size() - 1);
			current.remove(current.size() - 1);
			LOGGER.debug("Two-segment-loop detected, removing loop; new length of working set: " + current.size() + "; removed 2 entries");
			// Going back and forth through one portal, E.g. Overworld [gone] -> Nether [gone] -> Overworld
			current.add(toAdd);
			LOGGER.debug("Success, length of working set: " + current.size());
			return false;
		}
		if(current.size() >= 4 && doWaypointsMatch(current.get(current.size() - 4), toAdd)) {
			current.remove(current.size() - 1);
			current.remove(current.size() - 1);
			current.remove(current.size() - 1);
			current.remove(current.size() - 1);
			LOGGER.debug("Tour-segment-loop detected, removing loop; new length of working set: " + current.size() + "; removed 4 entries");
			// E.g. Overworld (Portal 1) [gone] -> Nether [gone] -> Overworld (Portal 2) [gone] -> Nether [gone] -> Overworld (Portal 1)
			current.add(toAdd);
			LOGGER.debug("Success, length of working set: " + current.size());
			return false;
		}
		current.add(toAdd);
		LOGGER.debug("Success, length of working set: " + current.size());
		return true;
	}

	public static boolean doWaypointsMatch(Waypoint one, Waypoint theOther) {
		return one.equals(theOther) || (one.isWaypointWithinRangeOf(theOther.position().dimension(), theOther.position().pos(), 5) && one.type() == theOther.type());
	}
}
