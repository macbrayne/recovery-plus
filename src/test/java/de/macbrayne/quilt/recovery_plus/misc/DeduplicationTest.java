package de.macbrayne.quilt.recovery_plus.misc;

import de.macbrayne.quilt.recovery_plus.data.Action;
import de.macbrayne.quilt.recovery_plus.data.CompassTrigger;
import de.macbrayne.quilt.recovery_plus.data.Trigger;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeduplicationTest {
	final ResourceKey<Registry<Level>> DIMENSION_REGISTRY;
	final ResourceKey<Level> THE_END;
	final ResourceKey<Level> THE_NETHER;

	final CompassTrigger NETHER_PORTAL;
	final CompassTrigger END_PORTAL;
	final CompassTrigger END_GATEWAY;
	public DeduplicationTest() {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		Bootstrap.validate();
		DIMENSION_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation("dimension"));
		THE_END = ResourceKey.create(DIMENSION_REGISTRY, new ResourceLocation("the_end"));
		THE_NETHER = ResourceKey.create(DIMENSION_REGISTRY, new ResourceLocation("the_nether"));

		NETHER_PORTAL = new CompassTrigger(new ResourceLocation("minecraft:nether_portal"), Trigger.MANUALLY, null, null, Action.ADD_TO_BACKLOG);
		END_GATEWAY = new CompassTrigger(new ResourceLocation("minecraft:end_gateway"), Trigger.MANUALLY, null, null, Action.ADD_TO_BACKLOG);
		END_PORTAL = new CompassTrigger(new ResourceLocation("minecraft:end_portal"), Trigger.INSIDE_BLOCK, LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.END_PORTAL).build()).build(), null, Action.ADD_TO_BACKLOG);
	}

	//region Adding Waypoints
	@Test
	void duplicatedWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var helper = new DedupConfig(list, new FakeEntity("DuplicatedWaypointsTest"));

		assertTrue(helper.addDedupWaypoint(THE_END, BlockPos.ZERO, NETHER_PORTAL), "Empty list should not trigger dedup");
		assertFalse(helper.addDedupWaypoint(THE_END, BlockPos.ZERO, NETHER_PORTAL), "Duplicated waypoint should trigger dedup");

		assertTrue(helper.addDedupWaypoint(THE_END, BlockPos.ZERO, END_GATEWAY), "Different Waypoint type should not trigger dedup");
		assertFalse(helper.addDedupWaypoint(THE_END, BlockPos.ZERO, END_GATEWAY), "Duplicated Waypoint should trigger dedup");
	}

	@Test
	void distantWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var helper = new DedupConfig(list, new FakeEntity("DistantWaypointsTest"));

		helper.addDedupWaypoint(THE_END, BlockPos.ZERO, NETHER_PORTAL);
		helper.addDedupWaypoint(THE_END, BlockPos.ZERO, NETHER_PORTAL);
		assertTrue(helper.addDedupWaypoint(THE_END, new BlockPos(10, 10, 10), NETHER_PORTAL), "Waypoint outside of radius should not trigger dedup");
	}

	@Test
	void differentDimensionWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var helper = new DedupConfig(list, new FakeEntity("DifferentDimensionsTest"));

		helper.addDedupWaypoint(THE_END, BlockPos.ZERO, NETHER_PORTAL);
		helper.addDedupWaypoint(THE_END, BlockPos.ZERO, NETHER_PORTAL);
		assertTrue(helper.addDedupWaypoint(THE_NETHER, BlockPos.ZERO, NETHER_PORTAL), "Waypoint in different dimension should not trigger dedup");
		assertEquals(2, list.size(), "Looping should return the working set to previous size (+1)");
	}

	@Test
	void loopedWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var helper = new DedupConfig(list, new FakeEntity("LoopedWaypointsTest"));
		helper.addDedupWaypoint(THE_END, BlockPos.ZERO, END_PORTAL);

		int previousSize = list.size();
		helper.addDedupWaypoint(THE_END, BlockPos.ZERO, NETHER_PORTAL);
		helper.addDedupWaypoint(THE_NETHER, BlockPos.ZERO, NETHER_PORTAL);
		assertFalse(helper.addDedupWaypoint(THE_END, BlockPos.ZERO, NETHER_PORTAL), "Two-segment-loop should not be added");
		assertEquals(previousSize + 1, list.size(), "Looping should return the working set to previous size (+1)");
	}

	@Test
	void bigLoopedWaypoints() {
		final var list = new ArrayList<Waypoint>();
		final var otherBlockPos = new BlockPos(100, 100, 100);
		final var helper = new DedupConfig(list, new FakeEntity("LoopedWaypointsTest"));
		helper.addDedupWaypoint(THE_END, BlockPos.ZERO, END_PORTAL);

		int previousSize = list.size();
		helper.addDedupWaypoint(THE_END, BlockPos.ZERO, NETHER_PORTAL);
		helper.addDedupWaypoint(THE_NETHER, otherBlockPos, NETHER_PORTAL);
		helper.addDedupWaypoint(THE_END, otherBlockPos, NETHER_PORTAL);
		helper.addDedupWaypoint(THE_NETHER, BlockPos.ZERO, NETHER_PORTAL);
		assertFalse(helper.addDedupWaypoint(THE_END, BlockPos.ZERO, NETHER_PORTAL), "Four-segment-loop should not be added");
		assertEquals(previousSize + 1, list.size(), "Looping should return the working set to previous size (+1)");
	}

	@Test
	void returnThroughPortal() {
		final var list = new ArrayList<Waypoint>();
		final var otherBlockPos = new BlockPos(100, 100, 100);
		final var helper = new DedupConfig(list, new FakeEntity("ReturnThroughPortal"));

		helper.addDedupWaypoint(THE_END, BlockPos.ZERO, THE_NETHER, BlockPos.ZERO, NETHER_PORTAL);
		assertFalse(helper.addDedupWaypoint(THE_NETHER, BlockPos.ZERO, THE_END, BlockPos.ZERO, NETHER_PORTAL), "Waypoints with target set should match ");
		assertEquals(0, list.size(), "Loop should be removed");
	}

	record DedupConfig(List<Waypoint> list, FakeEntity entity) {
		boolean addDedupWaypoint(ResourceKey<Level> dimension, BlockPos pos, CompassTrigger type) {
			return addDedupWaypoint(dimension, pos, null, null, type);
		}
		boolean addDedupWaypoint(ResourceKey<Level> dimension, BlockPos pos, ResourceKey<Level> targetDimension, BlockPos targetPos, CompassTrigger type) {
			return Deduplication.addDeduplicatedWaypoint(list(), dimension, pos, targetDimension, targetPos, type, entity());
		}
	}
	//endregion


}
