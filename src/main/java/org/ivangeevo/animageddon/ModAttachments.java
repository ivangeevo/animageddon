package org.ivangeevo.animageddon;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public class ModAttachments {

	private static final Identifier HAS_BAIT_ID = Identifier.of(AnimageddonMod.MOD_ID, "has_bait");
	public static final AttachmentType<Boolean> HAS_BAIT_ATTACHMENT = AttachmentRegistry.createPersistent(HAS_BAIT_ID, Codec.BOOL);

	public static void initialize() {
		AnimageddonMod.LOGGER.info("Registering {} attachments", AnimageddonMod.MOD_ID);
		// Technically this method can stay empty, but some developers like to notify
		// the console, that certain parts of the mod have been successfully initialized
	}





}