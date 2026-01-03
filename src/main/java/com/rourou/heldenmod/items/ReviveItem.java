package com.rourou.heldenmod.items;

import com.rourou.heldenmod.hearts.HeartManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.InteractionSide;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.entity.PartEntity;

import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.common.MinecraftForge;

public class ReviveItem extends Item {
    public ReviveItem(Properties props) {
        super(props);
        // Event for entity interact handled in ServerEventHandler for centralization
    }
}
