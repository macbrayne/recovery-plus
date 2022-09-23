package de.macbrayne.quilt.recovery_plus.misc;

import de.macbrayne.quilt.recovery_plus.data.CompassTrigger;

public interface ServerPlayerMixinTimerAccessor {
	void recoveryPlus_setTimer(CompassTrigger type, int ticksUntilMessage);
}
