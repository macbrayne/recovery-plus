package de.macbrayne.quilt.recovery_plus.data;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum Trigger {
	INSIDE_BLOCK("recovery_plus:inside_block"), MANUALLY("recovery_plus:manually");
	private String id;
	private static final Map<String, Trigger> LOOKUP = Maps.uniqueIndex(
			Arrays.asList(Trigger.values()),
			Trigger::getId
	);

	Trigger(String id) {
		this.id = id;
	}

	protected String getId() {
		return id;
	}

	static Trigger getTriggerFromId(String id) {
		return LOOKUP.get(id);
	}
}

