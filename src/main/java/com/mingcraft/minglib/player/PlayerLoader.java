package com.mingcraft.minglib.player;

import com.mingcraft.minglib.events.player.PlayerRegisterEvent;
import com.mingcraft.minglib.events.player.PlayerUnregisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerLoader {

    private static final Map<String, RealPlayer> playerMap = new HashMap<>();

    public static void registerPlayer(Player player) {
        RealPlayer realPlayer = new RealPlayer(player);
        playerMap.put(player.getName(), realPlayer);

        PlayerRegisterEvent event = new PlayerRegisterEvent(realPlayer);
        Bukkit.getPluginManager().callEvent(event);
    }

    public static void unregisterPlayer(Player player) {
        RealPlayer realPlayer = playerMap.get(player.getName());
        playerMap.remove(player.getName());

        PlayerUnregisterEvent event = new PlayerUnregisterEvent(realPlayer);
        Bukkit.getPluginManager().callEvent(event);
    }

    public static RealPlayer getRealPlayer(Player player) {
        return playerMap.get(player.getName());
    }

    public static String getUuid(Player player) {
        return playerMap.get(player.getName()).getUuid();
    }

    public static Player getPlayer(String uuid) {
        for (RealPlayer player : playerMap.values()) {
            if (player.getPlayer().getName().equals(uuid)) {
                return player.getPlayer();
            }
        }
        return null;
    }

}
