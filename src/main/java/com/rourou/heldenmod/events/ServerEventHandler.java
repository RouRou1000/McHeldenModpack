package com.rourou.heldenmod.events;

import com.rourou.heldenmod.ModConfigHandler;
import com.rourou.heldenmod.ModItems;
import com.rourou.heldenmod.hearts.HeartManager;
import com.rourou.heldenmod.combat.CombatManager;
import com.rourou.heldenmod.network.NetworkHandler;
import com.rourou.heldenmod.network.packets.SyncHeartsPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.EnderPearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.command.CommandSourceStack;
import net.minecraft.server.commands.CommandSourceStack;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class ServerEventHandler {
    public ServerEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        ServerPlayer sp = (ServerPlayer) event.getEntity();
        HeartManager.ensurePlayerLoaded(sp);
        // Sync hearts to client
        NetworkHandler.CHANNEL.sendToPlayer(new com.rourou.heldenmod.network.packets.SyncHeartsPacket(HeartManager.getHearts(sp)), sp);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        ServerPlayer sp = (ServerPlayer) event.getEntity();
        // Wenn im Combat und Logout-Strafe aktiv: sofortiger Tod + Herzverlust
        if (CombatManager.isInCombat(sp) && ModConfigHandler.COMMON.logoutPunishment.get() == ModConfigHandler.LogoutPunishment.DEATH) {
            // Herz entfernen
            HeartManager.removeHearts(sp, 1);
            // markiere persistente data
            sp.getServer().getPlayerList().broadcastSystemMessage(Component.literal(sp.getName().getString() + " hat beim Logout im Kampf ein Herz verloren."), false);
            // Falls 0 Herzen, handle elimination (Spectator/Ban)
            handleZeroHearts(sp);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getSource().getEntity() instanceof Player)) {
            // Wenn Schaden von Spieler kommt: tag beide
            return;
        }
        Player victim = (Player) event.getEntity();
        Player attacker = (Player) event.getSource().getEntity();
        if (attacker instanceof ServerPlayer) CombatManager.tag((ServerPlayer) attacker);
        if (victim instanceof ServerPlayer) CombatManager.tag((ServerPlayer) victim);
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return; // server-side only
        ServerPlayer victim = (ServerPlayer) event.getEntity();
        if (!(event.getSource().getEntity() instanceof Player)) {
            // Death by environment still costs heart
            HeartManager.removeHearts(victim, 1);
            checkHeartLossEffects(null, victim);
            handleZeroHearts(victim);
            return;
        }
        ServerPlayer killer = (ServerPlayer) event.getSource().getEntity();
        // Remove heart from victim
        HeartManager.removeHearts(victim, 1);
        // Effects / messages
        killer.level.playSound(null, victim.blockPosition(), SoundEvents.ANVIL_BREAK, SoundSource.PLAYERS, 1.0F, 0.8F);
        killer.displayClientMessage(Component.literal(String.format("⚔ %s hat %s ein Herz genommen", killer.getName().getString(), victim.getName().getString())), false);
        // Partikel & Slowness handled clientside -> we apply short slowness to killer as "kill animation"
        killer.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 20, 2));
        checkHeartLossEffects(killer, victim);
        handleZeroHearts(victim);
    }

    private void checkHeartLossEffects(ServerPlayer killer, ServerPlayer victim) {
        int heartsLeft = HeartManager.getHearts(victim);
        if (heartsLeft <= 0) {
            // Bigger animation / darkness
            victim.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DARKNESS, 60, 0));
            victim.sendSystemMessage(Component.literal(String.format("☠ %s ist gefallen", victim.getName().getString())));
        }
    }

    private void handleZeroHearts(ServerPlayer player) {
        int hearts = HeartManager.getHearts(player);
        if (hearts > 0) return;
        if (ModConfigHandler.COMMON.onZeroHearts.get() == ModConfigHandler.ZeroHeartsAction.SPECTATOR) {
            // Set to spectator
            player.setGameMode(net.minecraft.world.level.GameType.SPECTATOR);
        } else {
            // Ban player
            player.server.getPlayerList().getBannedPlayers().add(new net.minecraft.server.BanEntry(player.getGameProfile(), null, "Eliminated", null, "Reached 0 hearts"));
            player.connection.disconnect(Component.literal("Du wurdest eliminiert."));
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // Wenn Spieler im Combat -> keine Container öffnen
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        ServerPlayer sp = (ServerPlayer) event.getEntity();
        if (!CombatManager.isInCombat(sp)) return;
        BlockPos pos = event.getPos();
        net.minecraft.world.level.block.state.BlockState state = sp.level.getBlockState(pos);
        if (state.getBlock() instanceof ChestBlock ||
            state.getBlock() instanceof ShulkerBoxBlock ||
            state.getBlock() instanceof BarrelBlock ||
            state.getBlock() instanceof EnderChestBlock) {
            event.setCanceled(true);
            sp.displayClientMessage(Component.literal("Du kannst während des Kampfes keine Truhen öffnen."), true);
        }
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        // Disable ender pearls during combat
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        ServerPlayer sp = (ServerPlayer) event.getEntity();
        if (!CombatManager.isInCombat(sp)) return;
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof EnderPearlItem) {
            event.setCanceled(true);
            sp.displayClientMessage(Component.literal("Teleportation im Kampf ist nicht erlaubt."), true);
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        // Revive: Rechtsklick auf einen Spieler, der eliminiert ist -> revive falls ReviveItem in Hand
        if (event.getTarget() instanceof ServerPlayer && event.getEntity() instanceof ServerPlayer) {
            ServerPlayer target = (ServerPlayer) event.getTarget();
            ServerPlayer actor = (ServerPlayer) event.getEntity();
            if (HeartManager.getHearts(target) <= 0) {
                ItemStack hand = actor.getItemInHand(event.getHand());
                if (hand.getItem() == ModItems.REVIVE_ITEM.get()) {
                    // Revive target
                    HeartManager.setHearts(target, 1);
                    target.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
                    target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 600, 1));
                    target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 600, 1));
                    target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 100, 4)); // kurze Schutzzeit
                    actor.displayClientMessage(Component.literal("Du hast " + target.getName().getString() + " wiederbelebt!"), false);
                    target.displayClientMessage(Component.literal("Du wurdest wiederbelebt und besitzt 1 Heldenherz."), false);
                    if (!actor.isCreative()) hand.shrink(1);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onCommand(ServerChatEvent event) {
        // Nicht benutzt; stattdessen ServerCommandEvent kann verwendet werden um Commands zu blocken
    }

    @SubscribeEvent
    public void onServerCommand(net.minecraftforge.server.command.ServerCommandEvent event) {
        if (!(event.getSender() instanceof ServerPlayer)) return;
        ServerPlayer player = (ServerPlayer) event.getSender();
        if (CombatManager.isInCombat(player) && !player.hasPermissions(2)) {
            // Block commands (except admin)
            event.setCanceled(true);
            player.displayClientMessage(Component.literal("Während des Kampfes sind Commands verboten."), true);
        }
    }
}
