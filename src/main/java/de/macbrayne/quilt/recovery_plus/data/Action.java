package de.macbrayne.quilt.recovery_plus.data;

import com.google.common.collect.Maps;
import de.macbrayne.quilt.recovery_plus.components.Registry;
import de.macbrayne.quilt.recovery_plus.misc.Utils;
import de.macbrayne.quilt.recovery_plus.misc.Waypoint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public enum Action {
	ADD_TO_BACKLOG("recovery_plus:add_to_backlog", (trigger, player, level, location, targetLevel, targetLocation) -> {
		if(isDebug()) {
			printDebug(trigger, player, level, location, targetLevel, targetLocation);
		}
		Registry.WAYPOINTS.get(player).addDeduplicatedWaypoint(level, location, targetLevel, targetLocation, trigger);
	}), CLEAR_BACKLOG("recovery_plus:clear_backlog", (trigger, player, level, location, targetLevel, targetLocation) -> {

		if(isDebug()) {
			player.sendSystemMessage(Component.literal("Current Waypoint Backlog: " + Registry.WAYPOINTS.get(player).getWorkingCopy()));
			player.sendSystemMessage(Component.literal("Cleared backlog"));
		}
		Registry.WAYPOINTS.get(player).getWorkingCopy().clear();
	}), ON_DEATH("recovery_plus:set_working_set", (trigger, player, level, location, targetLevel, targetLocation) -> {
		if(isDebug()) {
			printDebug(trigger, player, level, location, targetLevel, targetLocation);
		}
		var waypoints = Registry.WAYPOINTS.get(player);
		waypoints.addDeduplicatedWaypoint(level, location, null, null, CompassTriggers.getTrigger(CompassTriggers.DEATH));
		waypoints.setProgress(0);
		waypoints.setLastDeath(waypoints.getWorkingCopy());
		waypoints.setWorkingCopy(new ArrayList<>());
	});

	private static void printDebug(CompassTrigger trigger, ServerPlayer player, ServerLevel level, BlockPos location, ServerLevel targetLevel, BlockPos targetLocation) {
		player.sendSystemMessage(Component.literal("Current Waypoint Backlog: " + Registry.WAYPOINTS.get(player).getWorkingCopy()));
		GlobalPos source = GlobalPos.of(level.dimension(), location);
		GlobalPos destination = GlobalPos.of(targetLevel == null ? null : targetLevel.dimension(), targetLocation);
		player.sendSystemMessage(Component.literal("Waypoint to add: " + new Waypoint(source, destination, trigger, Utils.getText(trigger).getString())));
	}

	private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("recovery_plus.debug"));
	private final String id;
	private final Config function;
	private static final Map<String, Action> LOOKUP = Maps.uniqueIndex(
			Arrays.asList(Action.values()),
			Action::getId
	);

	Action(String id, Config function) {
		this.id = id;
		this.function = function;
	}

	private String getId() {
		return id;
	}

	public void accept(CompassTrigger compassTrigger, ServerPlayer serverPlayer, ServerLevel level, BlockPos location, ServerLevel targetDimension, BlockPos targetLocation) {
		function.accept(compassTrigger, serverPlayer, level, location, targetDimension, targetLocation);
	}

	static Action getActionFromId(String id) {
		return LOOKUP.get(id);
	}

	public interface Config {
		void accept(CompassTrigger trigger, ServerPlayer player, ServerLevel level, BlockPos location, ServerLevel targetLevel, BlockPos targetLocation);
	}

	public static boolean isDebug() {
		return DEBUG;
	}
}
