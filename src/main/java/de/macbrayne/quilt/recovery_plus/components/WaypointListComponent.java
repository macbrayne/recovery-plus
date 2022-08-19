package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.misc.Waypoint;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public interface WaypointListComponent extends Component {

	List<Waypoint> getWorkingCopy();
	List<Waypoint> getLastDeath();

	int getProgress();

	void setWorkingCopy(List<Waypoint> workingCopy);

	void setLastDeath(List<Waypoint> lastDeath);

	void setProgress(int progress);

	void incrementProgress();

	boolean addDeduplicatedWaypoint(ServerLevel level, BlockPos pos, Waypoint.Type type);
}
