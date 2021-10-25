package com.mingcraft.minglib.player;

import com.mingcraft.minglib.MingLib;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    public PlayerListener(MingLib plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerLoader.registerPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerLoader.unregisterPlayer(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        PlayerLoader.unregisterPlayer(event.getPlayer());
    }

}
