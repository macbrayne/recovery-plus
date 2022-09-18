package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.misc.Waypoint;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WaypointListComponentImplTest {
	final ResourceKey<Registry<Level>> DIMENSION_REGISTRY;
	final ResourceKey<Level> THE_END;
	final Nameable ENTITY;
	public WaypointListComponentImplTest() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		Bootstrap.validate();
		DIMENSION_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation("dimension"));
		THE_END = ResourceKey.create(DIMENSION_REGISTRY, new ResourceLocation("the_end"));
		ENTITY = new FakeEntity("Test");
	}

	@Test
	void sameDimension() {
		final var list = new ArrayList<Waypoint>();

		assertTrue(WaypointListComponentImpl.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, ENTITY));
		assertFalse(WaypointListComponentImpl.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, ENTITY));

		list.add(new Waypoint(GlobalPos.of(THE_END, BlockPos.ZERO), Waypoint.Type.END_GATEWAY));

		// assertTrue(WaypointListComponentImpl.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.END_GATEWAY, ENTITY));
		// assertFalse(WaypointListComponentImpl.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.END_GATEWAY, ENTITY));
	}
}
