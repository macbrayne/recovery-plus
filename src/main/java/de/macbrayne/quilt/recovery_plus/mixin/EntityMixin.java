package de.macbrayne.quilt.recovery_plus.mixin;

import de.macbrayne.quilt.recovery_plus.data.CompassTriggers;
import de.macbrayne.quilt.recovery_plus.data.Trigger;
import de.macbrayne.quilt.recovery_plus.misc.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class EntityMixin {
	@Shadow
	public abstract void sendSystemMessage(Component message);

	@Unique
	private static final boolean debug = Boolean.parseBoolean(System.getProperty("recovery_plus.debug"));


	@Inject(method = "changeDimension", at=@At("HEAD"))
	public void dimension(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
		if((Entity) (Object) this instanceof ServerPlayer player && !((Entity) (Object) this).isRemoved()) {
			final var serverLevel = (ServerLevel) ((EntityAccessor) player).getLevel();
			final var location = player.position();
			for(var id : CompassTriggers.getIds()) {
				var trigger = CompassTriggers.getTrigger(id);
				if (trigger.trigger() == Trigger.INSIDE_BLOCK) {
					if(debug) {
						sendSystemMessage(Component.literal("Trigger: INSIDE_BLOCK, Block Pos: " + location + ", Predicate Matches: " + trigger.predicate().matches(serverLevel, location.x, location.y, location.z) + ", Block: " + player.level.getBlockState(player.getOnPos())).withStyle(ChatFormatting.AQUA));
					}
					if (trigger.predicate().matches(serverLevel, location.x, location.y, location.z) &&
							(trigger.inverted() == LocationPredicate.ANY || !trigger.inverted().matches(serverLevel, location.x, location.y, location.z))) {
						trigger.action().accept(trigger, player, (ServerLevel) player.level, new BlockPos(location), null, null);
					}
				}
			}
			Utils.doWaypointProgressionAndSync(player, serverLevel.dimension(), player.getOnPos());
		}
	}
}
