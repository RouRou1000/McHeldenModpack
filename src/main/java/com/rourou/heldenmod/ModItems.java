package com.rourou.heldenmod;

import com.rourou.heldenmod.items.HeroHeartItem;
import com.rourou.heldenmod.items.ReviveItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HeldenMod.MODID);

    public static final RegistryObject<Item> HERO_HEART = ITEMS.register("hero_heart", () ->
            new HeroHeartItem(new Item.Properties().stacksTo(64).tab(net.minecraft.world.item.CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<Item> REVIVE_ITEM = ITEMS.register("revive_item", () ->
            new ReviveItem(new Item.Properties().stacksTo(16).tab(net.minecraft.world.item.CreativeModeTab.TAB_MISC)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
