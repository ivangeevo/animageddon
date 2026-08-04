package org.btwr.animageddon.item.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.btwr.animageddon.AnimageddonMod;

import java.util.Arrays;
import java.util.List;

public record ComponentConstrainedIngredient(Ingredient base, List<ComponentExclusion> forbiddenComponents) implements CustomIngredient {

    @Override
    public boolean test(ItemStack itemStack) {
        if (!base.test(itemStack)) return false;
        for (ComponentExclusion ex : forbiddenComponents) {
            if (ex.violatedBy(itemStack)) return false;
        }
        return true;
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        // used for recipe book display etc - approximate is fine
        return Arrays.stream(base.getMatchingStacks())
                .filter(this::test)
                .toList();
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static final CustomIngredientSerializer<ComponentConstrainedIngredient> SERIALIZER =
            new CustomIngredientSerializer<>() {
                @Override
                public Identifier getIdentifier() {
                    return Identifier.of(AnimageddonMod.MOD_ID, "component_constrained");
                }

                @Override
                public MapCodec<ComponentConstrainedIngredient> getCodec(boolean allowEmpty) {
                    return RecordCodecBuilder.mapCodec(instance -> instance.group(
                            Ingredient.ALLOW_EMPTY_CODEC
                                    .fieldOf("base")
                                    .forGetter(ComponentConstrainedIngredient::base),
                            ComponentExclusion.CODEC
                                    .listOf()
                                    .optionalFieldOf("forbidden_components", List.of())
                                    .forGetter(ComponentConstrainedIngredient::forbiddenComponents)
                    ).apply(instance, ComponentConstrainedIngredient::new));
                }

                @Override
                public PacketCodec<RegistryByteBuf, ComponentConstrainedIngredient> getPacketCodec() {
                    return PacketCodec.tuple(
                            Ingredient.PACKET_CODEC, ComponentConstrainedIngredient::base,
                            ComponentExclusion.PACKET_CODEC.collect(PacketCodecs.toList()), ComponentConstrainedIngredient::forbiddenComponents,
                            ComponentConstrainedIngredient::new
                    );
                }
            };
}
