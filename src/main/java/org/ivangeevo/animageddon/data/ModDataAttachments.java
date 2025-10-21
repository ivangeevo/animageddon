package org.ivangeevo.animageddon.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;

public class ModDataAttachments {

    static Identifier HAS_BAIT_ID = Identifier.of(AnimageddonMod.MOD_ID, "has_bait");
    public static final AttachmentType<Boolean> HAS_BAIT = AttachmentRegistry.createPersistent(HAS_BAIT_ID, Codec.BOOL);

    public static final AttachmentType<MilkAttachedData> MILK_DATA = AttachmentRegistry.create(
            Identifier.of(AnimageddonMod.MOD_ID, "milk_data"),
            builder -> builder
                    .initializer(() -> MilkAttachedData.DEFAULT)
                    .persistent(MilkAttachedData.CODEC)
                    .syncWith(
                            MilkAttachedData.PACKET_CODEC,
                            AttachmentSyncPredicate.all()
                    )
    );

    public static void register() {
        AnimageddonMod.LOGGER.info("Registering {} attachments", AnimageddonMod.MOD_ID);
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized
    }
}
