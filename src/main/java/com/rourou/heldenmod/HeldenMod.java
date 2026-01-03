package com.rourou.heldenmod;

import com.rourou.heldenmod.commands.HeldenCommands;
import com.rourou.heldenmod.events.ServerEventHandler;
import com.rourou.heldenmod.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HeldenMod.MODID)
public class HeldenMod {
    public static final String MODID = "heldenmod";

    public HeldenMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigHandler.SPEC);

        // Register item registry etc.
        ModItems.register(bus);

        // Register global event handlers
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
        MinecraftForge.EVENT_BUS.register(NetworkHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Netzwerk initialisieren
        NetworkHandler.init();
        // Heart-System initial setup
        HeartManager.init();
        CombatManager.init();
    }
}
