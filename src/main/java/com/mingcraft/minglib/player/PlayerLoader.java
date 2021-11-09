package com.mingcraft.minglib.player;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerLoader {

    private static final ExecutorService executor = Executors.newFixedThreadPool(12);

    private static final Map<String, RealPlayer> playerMap = new ConcurrentHashMap<>();

    public static void shutdown() {
        executor.shutdown();
    }

    public static void registerPlayer(Player player) {
        MongoPlayer.registerPlayer(player);
    }

    public static void unregisterPlayer(Player player) {
        MongoPlayer.unregisterPlayer(player);
    }

    public static Map<String, RealPlayer> getPlayerMap() {
        return playerMap;
    }

    public static Collection<RealPlayer> getRealPlayers() {
        return playerMap.values();
    }

    public static Set<String> getUuids() {
        return playerMap.keySet();
    }

    public static RealPlayer getRealPlayer(Player player) {
        return playerMap.get(player.getName());
    }

    public static RealPlayer getRealPlayer(String uuid) {
        for (RealPlayer player : playerMap.values()) {
            if (player.getUuid().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

    public static RealPlayer getRealPlayerWithName(String name) {
        for (RealPlayer player : playerMap.values()) {
            if (player.getPlayer().getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public static String getUuid(Player player) {
        return playerMap.get(player.getName()).getUuid();
    }

    public static Player getPlayer(String uuid) {
        for (RealPlayer player : playerMap.values()) {
            if (player.getUuid().equals(uuid)) {
                return player.getPlayer();
            }
        }
        return null;
    }

}
