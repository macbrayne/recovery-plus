package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.Waypoint;
import dev.onyxstudios.cca.api.v3.component.Component;

import java.util.List;

public interface WaypointListComponent extends Component {

	List<Waypoint> getWorkingCopy();
	List<Waypoint> getLastDeath();

	int getProgress();

	void setWorkingCopy(List<Waypoint> workingCopy);

	void setLastDeath(List<Waypoint> lastDeath);

	void setProgress(int progress);

	void incrementProgress();
}
