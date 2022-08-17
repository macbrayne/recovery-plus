package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.misc.Waypoint;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

public interface WaypointListComponent extends Component {

	List<Waypoint> getWorkingCopy();
	List<Waypoint> getLastDeath();

	int getProgress();

	void setWorkingCopy(List<Waypoint> workingCopy);

	void setLastDeath(List<Waypoint> lastDeath);

	void setProgress(int progress);

	void incrementProgress();

	boolean addDeduplicatedWaypoint(ResourceKey<Level> dimension, BlockPos pos, Waypoint.Type type);
}
