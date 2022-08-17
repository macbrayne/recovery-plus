package de.macbrayne.quilt.recovery_plus;

import de.macbrayne.quilt.recovery_plus.Waypoint;

public interface ServerPlayerMixinTimerAccessor {
	void recoveryPlus_setTimer(Waypoint.Type type, int ticksUntilMessage);
}
