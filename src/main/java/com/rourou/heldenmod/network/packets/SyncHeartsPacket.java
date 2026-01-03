package com.rourou.heldenmod.network.packets;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncHeartsPacket {
    private final int hearts;

    public SyncHeartsPacket(int hearts) { this.hearts = hearts; }

    public static void encode(SyncHeartsPacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.hearts);
    }

    public static SyncHeartsPacket decode(FriendlyByteBuf buf) {
        return new SyncHeartsPacket(buf.readInt());
    }

    public static void handle(SyncHeartsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Client: speichere lokal für HUD-Render
            com.rourou.heldenmod.client.ClientData.CLIENT_HEARTS = pkt.hearts;
        });
        ctx.get().setPacketHandled(true);
    }
}
