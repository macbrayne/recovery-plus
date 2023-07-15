package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.data.CompassTrigger;
import de.macbrayne.quilt.recovery_plus.misc.Waypoint;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface WaypointListComponent extends Component {

	List<Waypoint> getWorkingCopy();
	List<Waypoint> getLastDeath();

	Player getProvider();

	int getProgress();

	void setWorkingCopy(List<Waypoint> workingCopy);

	void setLastDeath(List<Waypoint> lastDeath);

	void setProgress(int progress);

	void incrementProgress();

	boolean addDeduplicatedWaypoint(ServerLevel level, BlockPos pos, ServerLevel target, BlockPos targetPos, CompassTrigger type);
}
