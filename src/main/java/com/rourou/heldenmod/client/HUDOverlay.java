package com.rourou.heldenmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rourou.heldenmod.HeldenMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HeldenMod.MODID, value = Dist.CLIENT)
public class HUDOverlay {
    private static final ResourceLocation TEX_FULL = new ResourceLocation(HeldenMod.MODID, "textures/gui/heart_full.png");
    private static final ResourceLocation TEX_EMPTY = new ResourceLocation(HeldenMod.MODID, "textures/gui/heart_empty.png");
    private static final ResourceLocation TEX_LAST = new ResourceLocation(HeldenMod.MODID, "textures/gui/heart_last.png");

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGuiOverlayEvent.Post event) {
        if (!com.rourou.heldenmod.ModConfigHandler.COMMON.hudEnabled.get()) return;
        Minecraft mc = Minecraft.getInstance();
        int hearts = ClientData.CLIENT_HEARTS;
        int max = com.rourou.heldenmod.ModConfigHandler.COMMON.maxHearts.get();
        int x = 10;
        int y = mc.getWindow().getGuiScaledHeight() - 48; // links neben Hotbar

        for (int i = 0; i < max; i++) {
            ResourceLocation tex = (i < hearts) ? TEX_FULL : TEX_EMPTY;
            if (i == hearts - 1) tex = TEX_LAST;
            RenderSystem.setShaderTexture(0, tex);
            GuiComponent.blit(event.getGuiGraphics(), x + i * 18, y, 0, 0, 16, 16, 16, 16);
        }

        // Combat ActionBar
        if (ClientData.CLIENT_IN_COMBAT) {
            String s = String.format("IM KAMPF — %ds", ClientData.CLIENT_COMBAT_SECONDS);
            mc.gui.setOverlayMessage(net.minecraft.network.chat.Component.literal(s), false);
        }
    }
}
