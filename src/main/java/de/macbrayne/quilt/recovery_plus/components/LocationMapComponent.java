package de.macbrayne.quilt.recovery_plus.components;

import de.macbrayne.quilt.recovery_plus.misc.LocationMapUpdate;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public interface LocationMapComponent extends Component {
	void put(UUID uuid, GlobalPos pos);

	Optional<GlobalPos> remove(UUID uuid);

	Optional<GlobalPos> get(UUID uuid);

	boolean contains(UUID uuid);

	void registerUpdateListener(LocationMapUpdate listener, ItemStack stack);
}
