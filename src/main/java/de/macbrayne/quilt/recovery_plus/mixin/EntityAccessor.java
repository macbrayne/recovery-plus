package de.macbrayne.quilt.recovery_plus.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
	@Accessor
	Level getLevel();

	@Accessor
	BlockPos getPortalEntrancePos();

	@Invoker("findDimensionEntryPoint")
	PortalInfo invokeFindDimensionEntryPoint(ServerLevel destination);
}
