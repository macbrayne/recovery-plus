package de.macbrayne.quilt.recovery_plus.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompassTriggers extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
	private static final Logger LOGGER = LoggerFactory.getLogger("recovery_plus");
	private static final ResourceLocation FABRIC_ID = new ResourceLocation("recovery_plus", "compass_triggers");
	public static final ResourceLocation  DEATH = new ResourceLocation("minecraft", "death");
	public static final ResourceLocation END_GATEWAY = new ResourceLocation("minecraft", "end_gateway");
	public static final ResourceLocation NETHER_PORTAL = new ResourceLocation("minecraft", "nether_portal");

	private static final Map<ResourceLocation, CompassTrigger> PORTAL_TRIGGERS = new HashMap<>();
	public CompassTriggers() {
		super(new GsonBuilder().create(), "portal_blocks");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller profiler) {
		prepared.forEach((id, json) -> {
			try {
				PORTAL_TRIGGERS.put(id, CompassTrigger.fromJson(id, json));
			} catch (Exception exception) {
				LOGGER.error("Parsing error loading compass trigger {}", id, exception);
			}
		});
	}

	@Override
	@NotNull
	public ResourceLocation getFabricId() {
		return FABRIC_ID;
	}
	public static Set<ResourceLocation> getIds() {
		return PORTAL_TRIGGERS.keySet();
	}

	public static CompassTrigger getTrigger(ResourceLocation location) {
		return PORTAL_TRIGGERS.get(location);
	}
}
