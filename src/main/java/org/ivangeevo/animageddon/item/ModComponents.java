package org.ivangeevo.animageddon.item;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;

public class ModComponents {
	protected static void initialize() {
		AnimageddonMod.LOGGER.info("Registering {} components", AnimageddonMod.MOD_ID);
		// Technically this method can stay empty, but some developers like to notify
		// the console, that certain parts of the mod have been successfully initialized
	}


	public static final ComponentType<Integer> WOOL_COLOR = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(AnimageddonMod.MOD_ID, "wool_color"),
			ComponentType.<Integer>builder().codec(Codec.INT).build()
	);
}