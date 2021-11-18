package com.mingcraft.minglib.player;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.mingcraft.minglib.MingLib;
import com.mingcraft.minglib.colors.Color;
import com.mingcraft.minglib.events.player.PlayerRegisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;

public class PlayerListener implements Listener {

    public PlayerListener(MingLib plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private static final Map<String, Boolean> playerLoadingMap = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerLoader.registerPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!PlayerMover.isMoved(player)) {
            PlayerLoader.unregisterPlayer(player);
        }
        else {
            PlayerMover.removePlayerServerMoveState(player);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (!PlayerMover.isMoved(player)) {
            PlayerLoader.unregisterPlayer(player);
        }
        else {
            PlayerMover.removePlayerServerMoveState(player);
        }
    }

    @EventHandler
    public void loadingStart(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        playerLoadingMap.put(name, true);
        player.sendMessage(Color.colored("&6플레이어 정보 로딩중..."));
    }

    @EventHandler
    public void loadingEnd(PlayerRegisterEvent event) {
        Player player = event.getRealPlayer().getPlayer();
        String name = player.getName();
        Bukkit.getScheduler().scheduleSyncDelayedTask(MingLib.instance, () -> {
            playerLoadingMap.remove(name);
            player.sendMessage(Color.colored("&a플레이어 정보 로딩 완료!"));
        }, 20);
    }

    @EventHandler
    public void loadingStop(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        playerLoadingMap.remove(name);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        String name = event.getPlayer().getName();
        Boolean state = playerLoadingMap.get(name);
        if (state != null && state) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        String name = event.getPlayer().getName();
        Boolean state = playerLoadingMap.get(name);
        if (state != null && state) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneaking(PlayerToggleSneakEvent event) {
        String name = event.getPlayer().getName();
        Boolean state = playerLoadingMap.get(name);
        if (state != null && state) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSprinting(PlayerToggleSprintEvent event) {
        String name = event.getPlayer().getName();
        Boolean state = playerLoadingMap.get(name);
        if (state != null && state) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String name = event.getPlayer().getName();
        Boolean state = playerLoadingMap.get(name);
        if (state != null && state) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        String name = event.getPlayer().getName();
        Boolean state = playerLoadingMap.get(name);
        if (state != null && state) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            String name = player.getName();
            Boolean state = playerLoadingMap.get(name);
            if (state != null && state) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        String name = event.getPlayer().getName();
        Boolean state = playerLoadingMap.get(name);
        if (state != null && state) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        String name = event.getPlayer().getName();
        Boolean state = playerLoadingMap.get(name);
        if (state != null && state) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        String name = event.getPlayer().getName();
        Boolean state = playerLoadingMap.get(name);
        if (state != null && state) {
            event.setCancelled(true);
        }
    }

}
