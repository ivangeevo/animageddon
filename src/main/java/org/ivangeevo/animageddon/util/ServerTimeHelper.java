package org.ivangeevo.animageddon.util;

import net.minecraft.server.MinecraftServer;

public class ServerTimeHelper {
    private static MinecraftServer SERVER_INSTANCE;

    // Call this once when the server starts
    public static void setServerInstance(MinecraftServer server) {
        SERVER_INSTANCE = server;
    }

    public static long getOverworldTimeOfDayServerOnly() {
        if (SERVER_INSTANCE != null) {
            return SERVER_INSTANCE.getOverworld().getTimeOfDay();
        }
        return 0;
    }
}
