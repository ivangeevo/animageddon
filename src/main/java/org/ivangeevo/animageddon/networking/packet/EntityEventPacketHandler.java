package org.ivangeevo.animageddon.networking.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.ivangeevo.animageddon.entity.interfaces.CowEntityAdded;
import org.ivangeevo.animageddon.entity.interfaces.EntityAdded;
import org.ivangeevo.animageddon.entity.interfaces.SquidEntityAdded;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class EntityEventPacketHandler implements CustomPacketHandler {
    public static final int SET_ATTACK_TARGET_EVENT_ID = 0;
    public static final int SQUID_TENTACLE_ATTACK_EVENT_ID = 1;
    public static final int COW_KICK_ATTACK_EVENT_ID = 2;

    // Define a static method to handle packet reception
    public static void handleCustomPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        try {
            EntityEventPacketHandler packetHandler = new EntityEventPacketHandler();
            packetHandler.handleCustomPacket(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleCustomPacket(PacketByteBuf buf) throws IOException {
        if (buf.readBoolean()) {  // Read a boolean to distinguish between events on server and client
            // Handle packet on the client side
            handleClientPacket(buf);
        }

        /** else {
            // Handle packet on the server side
            handleServerPacket(buf);
        } **/
    }

    private void handleClientPacket(PacketByteBuf buf) {
        ClientWorld world = MinecraftClient.getInstance().world;
        int iEntityID = buf.readInt();

        assert world != null;
        Entity entity = world.getEntityById(iEntityID);

        if (entity != null) {
            int iEventType = buf.readByte();

            if (iEventType == SET_ATTACK_TARGET_EVENT_ID) {
                if (entity instanceof MobEntity attackingCreature) {

                    int iTargetEntityID = buf.readInt();

                    if (iTargetEntityID >= 0) {
                        Entity targetEntity = world.getEntityById(iTargetEntityID);

                        attackingCreature.setTarget((LivingEntity) targetEntity);
                    } else {
                        attackingCreature.setTarget(null);
                    }
                }
            } else if (iEventType == SQUID_TENTACLE_ATTACK_EVENT_ID) {
                if (entity instanceof SquidEntity attackingSquid) {

                    double dTargetXPos = ((double) (buf.readInt())) / 32D;
                    double dTargetYPos = ((double) (buf.readInt())) / 32D;
                    double dTargetZPos = ((double) (buf.readInt())) / 32D;

                    ((SquidEntityAdded) attackingSquid).onClientNotifiedOfTentacleAttack(dTargetXPos, dTargetYPos, dTargetZPos);
                }
            } else if (iEventType == COW_KICK_ATTACK_EVENT_ID) {
                if (entity instanceof CowEntity attackingCow) {

                    ((CowEntityAdded) attackingCow).onClientNotifiedOfKickAttack();
                }
            }
        }
    }

    public static void sendCustomPacketToClients(ServerWorld world, Entity entity, PacketByteBuf buf) {
        // Use Fabric's networking API to send the packet to all players tracking the entity
        world.getPlayers(player -> player.canSee(entity)).forEach(player -> {
            ServerPlayNetworking.send(player, new Identifier("btwr", "entity_event"), buf);
        });
    }

    private void handleServerPacket(PacketByteBuf buf) throws IOException {
        // Handle the packet on the server side
        // Existing server-side code...
    }

}
