package de.macbrayne.quilt.recovery_plus.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.resources.ResourceLocation;

public final class Registry implements EntityComponentInitializer {
	public static final ComponentKey<WaypointListComponent> WAYPOINTS =
			ComponentRegistry.getOrCreate(new ResourceLocation("recovery_plus", "waypoints"), WaypointListComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(WAYPOINTS, WaypointListComponentImpl::new);
	}
}
