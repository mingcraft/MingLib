package com.mingcraft.minglib.player;

import com.mingcraft.minglib.db.MongoPlayer;
import com.mingcraft.minglib.events.player.PlayerRegisterEvent;
import com.mingcraft.minglib.events.player.PlayerUnregisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerLoader {

    private static final Map<String, RealPlayer> playerMap = new HashMap<>();

    public static void registerPlayer(Player player) {
        RealPlayer realPlayer = new RealPlayer(player);
        playerMap.put(player.getName(), realPlayer);

        MongoPlayer.downloadPlayerData(realPlayer);

        PlayerRegisterEvent event = new PlayerRegisterEvent(realPlayer);
        Bukkit.getPluginManager().callEvent(event);
    }

    public static void unregisterPlayer(Player player) {
        RealPlayer realPlayer = playerMap.get(player.getName());
        playerMap.remove(player.getName());

        MongoPlayer.saveAndUnloadPlayerData(realPlayer);

        PlayerUnregisterEvent event = new PlayerUnregisterEvent(realPlayer);
        Bukkit.getPluginManager().callEvent(event);
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
