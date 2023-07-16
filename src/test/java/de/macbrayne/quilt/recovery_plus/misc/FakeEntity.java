package de.macbrayne.quilt.recovery_plus.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import org.jetbrains.annotations.NotNull;

public class FakeEntity implements Nameable {
	final Component name;

	public FakeEntity(String name) {
		this.name = Component.literal(name);
	}

	@Override
	@NotNull
	public Component getName() {
		return name;
	}
}
