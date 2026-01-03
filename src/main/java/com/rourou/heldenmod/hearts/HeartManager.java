package com.rourou.heldenmod.hearts;

import com.rourou.heldenmod.HeldenMod;
import com.rourou.heldenmod.ModConfigHandler;
import com.rourou.heldenmod.network.NetworkHandler;
import com.rourou.heldenmod.network.packets.SyncHeartsPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-seitiges Herz-Management.
 * Speichert Herzen in einer ConcurrentHashMap (UUID -> int). Synchronisiert bei Login und Heart-Änderungen.
 * Alternative: Persistente NBT in player.getPersistentData()
 */
public class HeartManager {
    private static final ConcurrentHashMap<UUID, Integer> hearts = new ConcurrentHashMap<>();

    public static void init() {
        // nothing yet
    }

    public static int getHearts(Player player) {
        if (player.level.isClientSide) return ModConfigHandler.COMMON.maxHearts.get(); // client fallback
        return hearts.getOrDefault(player.getUUID(), ModConfigHandler.COMMON.maxHearts.get());
    }

    public static void setHearts(ServerPlayer player, int value) {
        hearts.put(player.getUUID(), Math.max(0, value));
        // Persist to player's persistent data for safety
        CompoundTag persisted = player.getPersistentData();
        persisted.putInt("heldenmod_hearts", hearts.get(player.getUUID()));
        // Sync to client
        NetworkHandler.CHANNEL.sendToPlayer(new SyncHeartsPacket(hearts.get(player.getUUID())), player);
    }

    public static boolean addHearts(Player player, int amount) {
        if (player.level.isClientSide) return false;
        ServerPlayer sp = (ServerPlayer) player;
        int cur = getHearts(player);
        int max = ModConfigHandler.COMMON.maxHearts.get();
        if (cur >= max) return false;
        setHearts(sp, Math.min(max, cur + amount));
        return true;
    }

    public static void removeHearts(ServerPlayer player, int amount) {
        int cur = getHearts(player);
        int after = Math.max(0, cur - amount);
        setHearts(player, after);
    }

    public static void ensurePlayerLoaded(ServerPlayer player) {
        // load from persistent data if present
        CompoundTag persisted = player.getPersistentData();
        if (persisted.contains("heldenmod_hearts")) {
            hearts.put(player.getUUID(), persisted.getInt("heldenmod_hearts"));
        } else {
            hearts.put(player.getUUID(), ModConfigHandler.COMMON.maxHearts.get());
            setHearts(player, ModConfigHandler.COMMON.maxHearts.get());
        }
    }

    public static void removePlayer(ServerPlayer player) {
        hearts.remove(player.getUUID());
    }
}
