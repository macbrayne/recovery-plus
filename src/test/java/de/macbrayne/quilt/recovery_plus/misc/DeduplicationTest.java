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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
		final var config = new DedupConfig(list, entity);

		assertTrue(config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL), "Empty list should not trigger dedup");
		assertFalse(config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL), "Duplicated waypoint should trigger dedup");

		assertTrue(config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.END_GATEWAY), "Different Waypoint type should not trigger dedup");
		assertFalse(config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.END_GATEWAY), "Duplicated Waypoint should trigger dedup");
	}

	@Test
	void distantWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var entity = new FakeEntity("DistantWaypointsTest");
		final var config = new DedupConfig(list, entity);

		config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL);
		config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL);
		assertTrue(config.addDedupWaypoint(THE_END, new BlockPos(10, 10, 10), Waypoint.Type.NETHER_PORTAL), "Waypoint outside of radius should not trigger dedup");
	}

	@Test
	void differentDimensionWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var entity = new FakeEntity("DifferentDimensionsTest");
		final var config = new DedupConfig(list, entity);

		config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL);
		config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL);
		assertTrue(config.addDedupWaypoint(THE_NETHER, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL), "Waypoint in different dimension should not trigger dedup");
	}

	@Test
	void loopedWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var entity = new FakeEntity("LoopedWaypointsTest");
		final var config = new DedupConfig(list, entity);
		config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.END_PORTAL);

		int previousSize = list.size();
		config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL);
		config.addDedupWaypoint(THE_NETHER, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL);
		assertFalse(config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL), "Two-segment-loop should not be added");
		assertEquals(previousSize + 1, list.size(), "Looping should return the working set to previous size (+1)");
	}
	//endregion

	@Test
	void bigLoopedWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var entity = new FakeEntity("LoopedWaypointsTest");
		final var otherBlockPos = new BlockPos(100, 100, 100);
		final var config = new DedupConfig(list, entity);
		config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.END_PORTAL);

		int previousSize = list.size();
		config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL);
		config.addDedupWaypoint(THE_NETHER, otherBlockPos, Waypoint.Type.NETHER_PORTAL);
		config.addDedupWaypoint(THE_END, otherBlockPos, Waypoint.Type.NETHER_PORTAL);
		config.addDedupWaypoint(THE_NETHER, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL);
		assertFalse(config.addDedupWaypoint(THE_END, BlockPos.ZERO, Waypoint.Type.NETHER_PORTAL), "Four-segment-loop should not be added");
		assertEquals(previousSize + 1, list.size(), "Looping should return the working set to previous size (+1)");
	}

	record DedupConfig(List<Waypoint> list, FakeEntity entity) {
		boolean addDedupWaypoint(ResourceKey<Level> dimension, BlockPos pos, Waypoint.Type type) {
			return Deduplication.addDeduplicatedWaypoint(list(), dimension, pos, type, entity());
		}
	}

}
