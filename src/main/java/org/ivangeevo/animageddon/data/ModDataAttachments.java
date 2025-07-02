package org.ivangeevo.animageddon.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.AnimageddonMod;
import org.spongepowered.asm.mixin.Unique;

public class ModDataAttachments {

    static Identifier HAS_BAIT_ID = Identifier.of(AnimageddonMod.MOD_ID, "has_bait");
    public static final AttachmentType<Boolean> HAS_BAIT =
            AttachmentRegistry.createPersistent(HAS_BAIT_ID, Codec.BOOL);

   static Identifier GOT_MILK_ID = Identifier.of(AnimageddonMod.MOD_ID, "got_milk");
   public static final AttachmentType<Boolean> GOT_MILK =
           AttachmentRegistry.createPersistent(GOT_MILK_ID, Codec.BOOL);

    public static final AttachmentType<Integer> MILK_COOLDOWN =
            AttachmentRegistry.createDefaulted(Identifier.of(AnimageddonMod.MOD_ID, "milk_cooldown"), () -> 0);


}
