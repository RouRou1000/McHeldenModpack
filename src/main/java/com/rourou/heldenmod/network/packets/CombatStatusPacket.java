package com.rourou.heldenmod.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CombatStatusPacket {
    public final boolean inCombat;
    public final int secondsLeft;

    public CombatStatusPacket(boolean inCombat, int secondsLeft) {
        this.inCombat = inCombat;
        this.secondsLeft = secondsLeft;
    }

    public static void encode(CombatStatusPacket pkt, FriendlyByteBuf buf) {
        buf.writeBoolean(pkt.inCombat);
        buf.writeInt(pkt.secondsLeft);
    }

    public static CombatStatusPacket decode(FriendlyByteBuf buf) {
        return new CombatStatusPacket(buf.readBoolean(), buf.readInt());
    }

    public static void handle(CombatStatusPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            com.rourou.heldenmod.client.ClientData.CLIENT_IN_COMBAT = pkt.inCombat;
            com.rourou.heldenmod.client.ClientData.CLIENT_COMBAT_SECONDS = pkt.secondsLeft;
        });
        ctx.get().setPacketHandled(true);
    }
}
