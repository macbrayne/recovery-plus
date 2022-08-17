package de.macbrayne.quilt.recovery_plus.events;

import de.macbrayne.quilt.recovery_plus.Waypoint;
import de.macbrayne.quilt.recovery_plus.components.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class PlayerEvents {

	public static void afterRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
		if(alive) {
			return;
		}
		final var waypoints = Registry.WAYPOINTS.get(newPlayer);
		final var oldPos = GlobalPos.of(oldPlayer.level.dimension(), new BlockPos(oldPlayer.position()));
		waypoints.getWorkingCopy().add(new Waypoint(oldPos, Waypoint.Type.DEATH));
		waypoints.setProgress(0);
		waypoints.setLastDeath(waypoints.getWorkingCopy());
		waypoints.setWorkingCopy(new ArrayList<>());
	}
}
