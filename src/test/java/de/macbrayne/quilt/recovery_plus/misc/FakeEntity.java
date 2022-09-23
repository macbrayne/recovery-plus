package de.macbrayne.quilt.recovery_plus.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;

public class FakeEntity implements Nameable {
	final Component name;

	public FakeEntity(String name) {
		this.name = Component.literal(name);
	}

	@Override
	public Component getName() {
		return name;
	}
}
