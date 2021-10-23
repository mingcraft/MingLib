package com.mingcraft.minglib.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerLoader {

    private static final Map<String, RealPlayer> playerMap = new HashMap<>();

    public static void registerPlayer(Player player) {
        RealPlayer realPlayer = new RealPlayer(player);
        playerMap.put(player.getName(), realPlayer);
    }

    public static void unregisterPlayer(Player player) {
        playerMap.remove(player.getName());
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
