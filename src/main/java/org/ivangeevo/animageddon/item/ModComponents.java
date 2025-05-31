package org.ivangeevo.animageddon.item;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;

public class ModComponents {

	public static final ComponentType<Boolean> HAS_BAIT_COMPONENT = ComponentType.<Boolean>builder().codec(Codec.BOOL).build();

	public static void initialize() {
		AnimageddonMod.LOGGER.info("Registering {} components", AnimageddonMod.MOD_ID);
		// Technically this method can stay empty, but some developers like to notify
		// the console, that certain parts of the mod have been successfully initialized

		registerDataComponent(ofModID("has_bait"), HAS_BAIT_COMPONENT);
	}

	/** Helper method to register Data Components faster **/
	private static void registerDataComponent(String id, ComponentType<?> componentType) {
		Registry.register(Registries.DATA_COMPONENT_TYPE, id, componentType);
	}

	/** Returns a String id of the animageddon mod namespace with the specified name(path) **/
	private static String ofModID(String path) {
		return AnimageddonMod.MOD_ID + ":" + path;
	}

}