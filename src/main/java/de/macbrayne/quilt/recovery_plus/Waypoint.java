package de.macbrayne.quilt.recovery_plus;

import com.google.common.collect.Maps;
import net.minecraft.core.GlobalPos;

import java.util.Arrays;
import java.util.Map;

public record Waypoint(GlobalPos position, Type type) {
	public enum Type {
		NETHER_PORTAL("minecraft:nether_portal"), END_GATEWAY("minecraft:end_gateway"), END_PORTAL("minecraft:end_portal");

		static private final Map<String, Type> REVERSE = Maps.uniqueIndex(Arrays.asList(Type.values()), Type::getId);
		private final String id;

		private Type(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public static Type fromId(String id) {
			return REVERSE.get(id);
		}
	}
}
