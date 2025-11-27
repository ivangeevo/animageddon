package org.btwr.animageddon.data;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;
import org.btwr.animageddon.AnimageddonMod;
import org.btwr.animageddon.data.attachments.SpiderWebData;
import org.btwr.animageddon.data.attachments.hunger.LivingEntityHungerData;
import org.btwr.animageddon.data.attachments.ChickenEggAttachedData;
import org.btwr.animageddon.data.attachments.CowMilkAttachedData;

import static org.btwr.animageddon.data.interfaces.HungerData.DEFAULT_FOOD_MULTIPLIER;
import static org.btwr.animageddon.data.interfaces.HungerData.DEFAULT_GRAZE_DURATION;

public class ModDataAttachments {

    public static final AttachmentType<Boolean> HAS_BAIT = AttachmentRegistry.createPersistent(
            Identifier.of(AnimageddonMod.MOD_ID, "has_bait"),
            Codec.BOOL
    );

    public static final AttachmentType<CowMilkAttachedData> MILK_DATA = AttachmentRegistry.create(
            Identifier.of(AnimageddonMod.MOD_ID, "milk_data"),
            builder -> builder
                    .initializer(CowMilkAttachedData::forDefault)
                    .persistent(CowMilkAttachedData.CODEC)
                    .syncWith(CowMilkAttachedData.PACKET_CODEC, AttachmentSyncPredicate.all())
    );

    public static final AttachmentType<ChickenEggAttachedData> CHICKEN_EGG_DATA = AttachmentRegistry.create(
            Identifier.of(AnimageddonMod.MOD_ID, "chicken_egg_data"),
            builder -> builder
                    .initializer(ChickenEggAttachedData::forDefault)
                    .persistent(ChickenEggAttachedData.CODEC)
                    .syncWith(ChickenEggAttachedData.PACKET_CODEC, AttachmentSyncPredicate.all())
    );

    public static final AttachmentType<LivingEntityHungerData> LIVING_ENTITY_HUNGER_DATA = AttachmentRegistry.create(
            Identifier.of(AnimageddonMod.MOD_ID, "living_entity_hunger_data"),
            builder -> builder
                    .initializer(() -> new LivingEntityHungerData(DEFAULT_FOOD_MULTIPLIER, DEFAULT_GRAZE_DURATION))
                    .persistent(LivingEntityHungerData.CODEC)
                    .syncWith(LivingEntityHungerData.PACKET_CODEC, AttachmentSyncPredicate.all())
    );

    public static final AttachmentType<SpiderWebData> SPIDER_WEB_DATA = AttachmentRegistry.create(
            Identifier.of(AnimageddonMod.MOD_ID, "spider_web_data"),
            builder -> builder
                    .initializer(() -> new SpiderWebData(false, SpiderWebData.TIME_BETWEEN_WEBS))
                    .persistent(SpiderWebData.CODEC)
                    .syncWith(SpiderWebData.PACKET_CODEC, AttachmentSyncPredicate.all())
    );

    public static void register() {
        AnimageddonMod.LOGGER.info("Registering {} attachments", AnimageddonMod.MOD_ID);
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized
    }

}