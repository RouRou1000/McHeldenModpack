package com.rourou.heldenmod.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.rourou.heldenmod.hearts.HeartManager;

public class HeroHeartItem extends Item {
    public HeroHeartItem(Properties props) {
        super(props);
    }

    // Rechtsklick auf Spieler um Herzen zu geben
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        // Einfacher Verbrauch: Wenn auf sich selbst, füge Herz hinzu (nur server)
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            boolean added = HeartManager.addHearts(player, 1);
            if (added) {
                if (!player.isCreative()) stack.shrink(1);
                player.displayClientMessage(Component.literal("Du hast dir ein Heldenherz gegeben."), true);
            } else {
                player.displayClientMessage(Component.literal("Du kannst keine weiteren Herzen erhalten."), true);
            }
        }
        return InteractionResultHolder.success(stack);
    }
}
