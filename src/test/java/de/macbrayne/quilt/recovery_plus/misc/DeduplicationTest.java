package de.macbrayne.quilt.recovery_plus.misc;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeduplicationTest {
	final ResourceKey<Registry<Level>> DIMENSION_REGISTRY;
	final ResourceKey<Level> THE_END;
	final ResourceKey<Level> THE_NETHER;
	public DeduplicationTest() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		Bootstrap.validate();
		DIMENSION_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation("dimension"));
		THE_END = ResourceKey.create(DIMENSION_REGISTRY, new ResourceLocation("the_end"));
		THE_NETHER = ResourceKey.create(DIMENSION_REGISTRY, new ResourceLocation("the_nether"));
	}

	//region Adding Waypoints
	@Test
	void duplicatedWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var entity = new FakeEntity("DuplicatedWaypointsTest");

		assertTrue(Deduplication.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, entity), "Empty list should not trigger dedup");
		assertFalse(Deduplication.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, entity), "Duplicated waypoint should trigger dedup");

		assertTrue(Deduplication.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.END_GATEWAY, entity), "Different Waypoint type should not trigger dedup");
		assertFalse(Deduplication.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.END_GATEWAY, entity), "Duplicated Waypoint should trigger dedup");
	}

	@Test
	void distantWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var entity = new FakeEntity("DistantWaypointsTest");

		Deduplication.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, entity);
		Deduplication.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, entity);
		assertTrue(Deduplication.addDeduplicatedWaypoint(list, THE_END, new BlockPos(10, 10, 10), Waypoint.Type.NETHER_PORTAL, entity), "Waypoint outside of radius should not trigger dedup");
	}

	@Test
	void differentDimensionWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var entity = new FakeEntity("DifferentDimensionsTest");

		Deduplication.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, entity);
		Deduplication.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, entity);
		assertTrue(Deduplication.addDeduplicatedWaypoint(list, THE_NETHER, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, entity), "Waypoint in different dimension should not trigger dedup");
	}

	@Test
	void loopedWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var entity = new FakeEntity("LoopedWaypointsTest");

		Deduplication.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, entity);
		Deduplication.addDeduplicatedWaypoint(list, THE_NETHER, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, entity);
		assertFalse(Deduplication.addDeduplicatedWaypoint(list, THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL, entity), "Two-segment-loop should not be added");
	}
	//endregion
}
