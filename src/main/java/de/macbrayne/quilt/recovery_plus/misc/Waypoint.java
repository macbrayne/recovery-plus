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

public record Waypoint(GlobalPos position, CompassTrigger type, String translation) {
	public boolean isWaypointWithinRangeOf(Waypoint waypoint, double distance) {
		return isWaypointWithinRangeOf(waypoint.position().dimension(), waypoint.position().pos(), distance);
	}

	public boolean isWaypointWithinRangeOf(ResourceKey<Level> dimension, BlockPos pos, double distance) {
		return position().dimension() == dimension && position().pos().closerThan(pos, distance);
	}
}
