package com.rourou.heldenmod.network;

import com.rourou.heldenmod.HeldenMod;
import com.rourou.heldenmod.network.packets.CombatStatusPacket;
import com.rourou.heldenmod.network.packets.SyncHeartsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.server.level.ServerPlayer;

public class NetworkHandler {
    private static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(HeldenMod.MODID, "network"),
                () -> PROTOCOL,
                PROTOCOL::equals,
                PROTOCOL::equals);
        int id = 0;
        CHANNEL.registerMessage(id++, SyncHeartsPacket.class, SyncHeartsPacket::encode, SyncHeartsPacket::decode, SyncHeartsPacket::handle);
        CHANNEL.registerMessage(id++, CombatStatusPacket.class, CombatStatusPacket::encode, CombatStatusPacket::decode, CombatStatusPacket::handle);
    }

    public static void sendToPlayer(Object packet, ServerPlayer player) {
        CHANNEL.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
