package de.macbrayne.quilt.recovery_plus.components;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;

public class FakeEntity implements Nameable {
	final String name;

	public FakeEntity(String name) {
		this.name = name;
	}

	@Override
	public Component getName() {
		return Component.literal(name);
	}
}
