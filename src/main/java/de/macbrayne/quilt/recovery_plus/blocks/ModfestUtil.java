package de.macbrayne.quilt.recovery_plus.blocks;

import de.macbrayne.quilt.recovery_plus.components.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModfestUtil extends PressurePlateBlock {
	public static final Logger LOGGER = LoggerFactory.getLogger("recovery_plus");
	public ModfestUtil(Properties properties) {
		super(Sensitivity.MOBS, properties);
	}

	@Override
	protected void checkPressed(@Nullable Entity entity, Level world, BlockPos pos, BlockState state, int output) {
		int signalStrength = this.getSignalStrength(world, pos);
		boolean isPowering = output > 0;
		boolean shouldPower = signalStrength > 0;
		if (output != signalStrength) {
			BlockState blockState = this.setSignalForState(state, signalStrength);
			world.setBlock(pos, blockState, 2);
			this.updateNeighbours(world, pos);
			world.setBlocksDirty(pos, state, blockState);
		}

		if (!shouldPower && isPowering) {
			world.gameEvent(entity, GameEvent.BLOCK_DEACTIVATE, pos);
		} else if (shouldPower && !isPowering) {
			world.gameEvent(entity, GameEvent.BLOCK_ACTIVATE, pos);

			if(entity instanceof ServerPlayer player) {
				var workingCopy = Registry.WAYPOINTS.get(player).getWorkingCopy();
				if(!workingCopy.isEmpty()) {
					player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.2F, 0.8F);

					workingCopy.clear();
					LOGGER.trace("Cleared working copy of " + player.getName().getString());
				}
			}
		}

		if (shouldPower) {
			world.scheduleTick(new BlockPos(pos), this, this.getPressedTime());
		}
	}

	@Override
	protected int getPressedTime() {
		return 5;
	}
}
