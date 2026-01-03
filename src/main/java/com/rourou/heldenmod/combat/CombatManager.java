package com.rourou.heldenmod.combat;

import com.rourou.heldenmod.ModConfigHandler;
import com.rourou.heldenmod.network.NetworkHandler;
import com.rourou.heldenmod.network.packets.CombatStatusPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verwaltet Combat-Tags (UUID -> expiryTimeMillis)
 */
public class CombatManager {
    private static final Map<UUID, Long> inCombat = new ConcurrentHashMap<>();

    public static void init() {}

    public static void tag(ServerPlayer player) {
        long expiry = System.currentTimeMillis() + ModConfigHandler.COMMON.combatDurationSeconds.get() * 1000L;
        inCombat.put(player.getUUID(), expiry);
        // Sende Packet an Spieler, damit HUD aktualisiert
        NetworkHandler.CHANNEL.sendToPlayer(new CombatStatusPacket(true, (int)((expiry - System.currentTimeMillis())/1000)), player);
    }

    public static boolean isInCombat(ServerPlayer player) {
        Long expiry = inCombat.get(player.getUUID());
        if (expiry == null) return false;
        if (System.currentTimeMillis() > expiry) {
            inCombat.remove(player.getUUID());
            NetworkHandler.CHANNEL.sendToPlayer(new CombatStatusPacket(false, 0), player);
            return false;
        }
        return true;
    }

    public static int getRemainingSeconds(ServerPlayer player) {
        Long expiry = inCombat.get(player.getUUID());
        if (expiry == null) return 0;
        long rem = expiry - System.currentTimeMillis();
        return (int) Math.max(0, rem / 1000);
    }

    public static void removeTag(ServerPlayer player) {
        inCombat.remove(player.getUUID());
        NetworkHandler.CHANNEL.sendToPlayer(new CombatStatusPacket(false, 0), player);
    }
}
