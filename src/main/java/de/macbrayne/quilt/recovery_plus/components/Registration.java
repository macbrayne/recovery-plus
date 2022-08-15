package de.macbrayne.quilt.recovery_plus.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.resources.ResourceLocation;

public final class Registration implements EntityComponentInitializer {
	public static final ComponentKey<PositionListComponent> WAYPOINTS =
			ComponentRegistry.getOrCreate(new ResourceLocation("recovery_plus", "waypoints"), PositionListComponent.class);

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerForPlayers(WAYPOINTS, WaypointListComponent::new);
	}
}
