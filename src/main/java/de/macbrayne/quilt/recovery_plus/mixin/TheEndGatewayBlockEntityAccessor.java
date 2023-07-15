package de.macbrayne.quilt.recovery_plus.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TheEndGatewayBlockEntity.class)
public interface TheEndGatewayBlockEntityAccessor {
	@Accessor
	BlockPos getExitPortal();
}
