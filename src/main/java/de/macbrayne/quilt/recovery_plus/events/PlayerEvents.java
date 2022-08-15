package de.macbrayne.quilt.recovery_plus.events;

import de.macbrayne.quilt.recovery_plus.components.Registration;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class PlayerEvents {

	public static void afterRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
		final var WAYPOINTS = Registration.WAYPOINTS.get(newPlayer);
		WAYPOINTS.setLastDeath(WAYPOINTS.getWorkingCopy());
		WAYPOINTS.setWorkingCopy(new ArrayList<>());
	}
}
