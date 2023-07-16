package de.macbrayne.quilt.recovery_plus.misc;

import com.google.common.collect.Maps;
import de.macbrayne.quilt.recovery_plus.data.CompassTrigger;
import de.macbrayne.quilt.recovery_plus.data.CompassTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Map;

public record Waypoint(GlobalPos position, GlobalPos target, CompassTrigger type, String translation) {
	public boolean isWaypointWithinRangeOf(Waypoint waypoint, double distance) {
		boolean targetsMatch = target() != null && waypoint.target() != null ? doTargetsOverlap(waypoint, distance) : true;
		return isWaypointWithinRangeOf(waypoint.position().dimension(), waypoint.position().pos(), distance) && targetsMatch;
	}

	public boolean isWaypointWithinRangeOf(ResourceKey<Level> dimension, BlockPos pos, double distance) {
		return position().dimension() == dimension && position().pos().closerThan(pos, distance);
	}

	public boolean doTargetsOverlap(Waypoint waypoint, double distance) {
		if(target != null && waypoint.target != null) {
			return isWaypointWithinRangeOf(waypoint.target().dimension(), waypoint.target().pos(), distance) ||
				waypoint.isWaypointWithinRangeOf(target().dimension(), target().pos(), distance);
		}
		return false;
	}
}
