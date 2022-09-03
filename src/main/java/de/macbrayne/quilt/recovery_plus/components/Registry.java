package de.macbrayne.quilt.recovery_plus.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.minecraft.resources.ResourceLocation;

public final class Registry implements EntityComponentInitializer, ScoreboardComponentInitializer {
	public static final ComponentKey<WaypointListComponent> WAYPOINTS =
			ComponentRegistry.getOrCreate(new ResourceLocation("recovery_plus", "waypoints"), WaypointListComponent.class);
	public static final ComponentKey<LocationMapComponent> TRACKED_ENTITIES =
			ComponentRegistry.getOrCreate(new ResourceLocation("recovery_plus", "tracked_entities"), LocationMapComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(WAYPOINTS, WaypointListComponentImpl::new);
	}

	@Override
	public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
		registry.registerScoreboardComponent(TRACKED_ENTITIES, LocationMapComponentImpl::new);
	}
}
