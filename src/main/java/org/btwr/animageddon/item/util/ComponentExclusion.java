package org.btwr.animageddon.item.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record ComponentExclusion(Identifier typeId, Optional<JsonElement> rawValue) {

    private static final Codec<JsonElement> JSON_ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
            dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
            json -> new Dynamic<>(JsonOps.INSTANCE, json)
    );

    public static final Codec<ComponentExclusion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("type").forGetter(ComponentExclusion::typeId),
            JSON_ELEMENT_CODEC.optionalFieldOf("value").forGetter(ComponentExclusion::rawValue)
    ).apply(instance, ComponentExclusion::new));

    // JsonElement <-> String over the wire, reusing vanilla's STRING packet codec
    private static final PacketCodec<ByteBuf, JsonElement> JSON_PACKET_CODEC =
            PacketCodecs.STRING.xmap(JsonParser::parseString, JsonElement::toString);

    public static final PacketCodec<RegistryByteBuf, ComponentExclusion> PACKET_CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, ComponentExclusion::typeId,
            JSON_PACKET_CODEC.collect(PacketCodecs::optional), ComponentExclusion::rawValue,
            ComponentExclusion::new
    );

    @SuppressWarnings("unchecked")
    public boolean violatedBy(ItemStack stack) {
        ComponentType<?> type = Registries.DATA_COMPONENT_TYPE.get(typeId);
        if (type == null || !stack.contains(type)) return false;
        if (rawValue.isEmpty()) return true;

        Object current = stack.get(type);
        Codec<Object> codec = (Codec<Object>) type.getCodec().orElse(null);
        if (codec == null) return false;

        var decoded = codec.parse(JsonOps.INSTANCE, rawValue.get()).result();
        return decoded.isPresent() && java.util.Objects.equals(current, decoded.get());
    }
}