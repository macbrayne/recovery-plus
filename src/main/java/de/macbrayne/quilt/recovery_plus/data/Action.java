package de.macbrayne.quilt.recovery_plus.data;

import com.google.common.collect.Maps;
import de.macbrayne.quilt.recovery_plus.components.Registry;
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
	ADD_TO_BACKLOG("recovery_plus:add_to_backlog", (trigger, player, level, location) -> {

		if(isDebug()) {
			player.sendSystemMessage(Component.literal("Current Waypoint Backlog: " + Registry.WAYPOINTS.get(player).getWorkingCopy()));
			player.sendSystemMessage(Component.literal("Waypoint to add: " + new Waypoint(GlobalPos.of(level.dimension(), location), trigger, "")));
		}
		Registry.WAYPOINTS.get(player).addDeduplicatedWaypoint(level, location, trigger);
	}), CLEAR_BACKLOG("recovery_plus:clear_backlog", (trigger, player, level, location) -> {

		if(isDebug()) {
			player.sendSystemMessage(Component.literal("Current Waypoint Backlog: " + Registry.WAYPOINTS.get(player).getWorkingCopy()));
			player.sendSystemMessage(Component.literal("Cleared backlog"));
		}
		Registry.WAYPOINTS.get(player).getWorkingCopy().clear();
	}), ON_DEATH("recovery_plus:set_working_set", (trigger, player, level, location) -> {
		if(isDebug()) {
			player.sendSystemMessage(Component.literal("Player Died"));
			player.sendSystemMessage(Component.literal("Current Waypoint Backlog: " + Registry.WAYPOINTS.get(player).getWorkingCopy()));
			player.sendSystemMessage(Component.literal("Waypoint to add: " + new Waypoint(GlobalPos.of(level.dimension(), location), trigger, "")));
		}
		var waypoints = Registry.WAYPOINTS.get(player);
		waypoints.addDeduplicatedWaypoint(level, location, CompassTriggers.getTrigger(CompassTriggers.DEATH));
		waypoints.setProgress(0);
		waypoints.setLastDeath(waypoints.getWorkingCopy());
		waypoints.setWorkingCopy(new ArrayList<>());
	});
	public static final boolean debug = Boolean.parseBoolean(System.getProperty("recovery_plus.debug"));
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

	public void accept(CompassTrigger compassTrigger, ServerPlayer serverPlayer, ServerLevel dimension, BlockPos location) {
		function.accept(compassTrigger, serverPlayer, dimension, location);
	}

	static Action getActionFromId(String id) {
		return LOOKUP.get(id);
	}

	public interface Config {
		void accept(CompassTrigger trigger, ServerPlayer player, ServerLevel dimension, BlockPos location);
	}

	public static boolean isDebug() {
		return debug;
	}
}
