package org.ivangeevo.animageddon.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;

public class ModDataAttachments {

    static Identifier HAS_BAIT_ID = Identifier.of(AnimageddonMod.MOD_ID, "has_bait");
    public static final AttachmentType<Boolean> HAS_BAIT = AttachmentRegistry.createPersistent(HAS_BAIT_ID, Codec.BOOL);

}
