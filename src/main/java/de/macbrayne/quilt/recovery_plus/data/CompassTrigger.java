package de.macbrayne.quilt.recovery_plus.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record CompassTrigger(ResourceLocation location, Trigger trigger, LocationPredicate predicate, LocationPredicate inverted, Action action) {
	public static final Logger LOGGER = LoggerFactory.getLogger("recovery_plus");
	public static CompassTrigger fromJson(ResourceLocation location, JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "compass trigger");
			var trigger =  Trigger.getTriggerFromId(jsonObject.get("trigger").getAsString());
			var predicate = LocationPredicate.fromJson(jsonObject.get("predicate"));
			var inverted = LocationPredicate.fromJson(jsonObject.get("inverted"));
			var action = Action.getActionFromId(jsonObject.get("action").getAsString());
			return new CompassTrigger(location, trigger, predicate, inverted, action);
		} else {
			return null;
		}
	}
}
