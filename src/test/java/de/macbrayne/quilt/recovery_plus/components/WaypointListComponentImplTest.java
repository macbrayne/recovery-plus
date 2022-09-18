package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.misc.Waypoint;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WaypointListComponentImplTest {
	public WaypointListComponentImplTest() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		Bootstrap.validate();
	}

	@Test
	void checkDeduplication() {
		final var list = new ArrayList<Waypoint>();
		final ResourceKey<Registry<Level>> DIMENSION_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation("dimension"));
		final var theEnd = ResourceKey.create(DIMENSION_REGISTRY, new ResourceLocation("the_end"));
		final var fakeEntity = new FakeEntity("Test");

		assertTrue(WaypointListComponentImpl.addDeduplicatedWaypoint(list, theEnd, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, fakeEntity));
		assertFalse(WaypointListComponentImpl.addDeduplicatedWaypoint(list, theEnd, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, fakeEntity));

		list.add(new Waypoint(GlobalPos.of(theEnd, BlockPos.ZERO), Waypoint.Type.END_GATEWAY));

		// assertTrue(WaypointListComponentImpl.addDeduplicatedWaypoint(list, theEnd, BlockPos.ZERO, Waypoint.Type.END_GATEWAY, fakeEntity));
		// assertFalse(WaypointListComponentImpl.addDeduplicatedWaypoint(list, theEnd, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, fakeEntity));

	}
}
