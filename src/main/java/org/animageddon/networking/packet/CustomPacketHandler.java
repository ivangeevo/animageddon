package org.animageddon.networking.packet;


import net.minecraft.network.PacketByteBuf;

import java.io.IOException;

public interface CustomPacketHandler {
    void handleCustomPacket(PacketByteBuf buf) throws IOException;
}
