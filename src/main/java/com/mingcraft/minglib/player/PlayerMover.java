package com.mingcraft.minglib.player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mingcraft.minglib.MingLib;
import com.mingcraft.minglib.colors.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerMover {

    private static final List<String> CONNECTED_CHANNEL = new ArrayList<>();
    private static final List<String> movedPlayer = new ArrayList<>();

    public static void setConnectedChannel(List<String> channels) {
        CONNECTED_CHANNEL.clear();
        CONNECTED_CHANNEL.addAll(channels);
    }

    public static List<String> getConnectedChannel() {
        return CONNECTED_CHANNEL;
    }

    public static boolean hasConnection(String channel) {
        return CONNECTED_CHANNEL.contains(channel);
    }

    public static void enablePlayerServerMoveState(Player player) {
        movedPlayer.add(player.getName());
    }

    public static void removePlayerServerMoveState(Player player) {
        movedPlayer.remove(player.getName());
    }

    public static boolean isMoved(Player player) {
        return movedPlayer.contains(player.getName());
    }

    public static void movePlayerTo(Player player, String channel) {
        if (isServerConnected(channel)) {
            enablePlayerServerMoveState(player);
            MongoPlayer.unregisterPlayerWhenServerMove(player, channel.toUpperCase());
        }
        else {
            player.sendMessage(Color.colored("&c서버 점검중입니다. 공지를 확인해주세요."));
        }
    }

    public static void moveServer(Player player, String channel) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(channel.toUpperCase());
        player.sendPluginMessage(MingLib.instance, MingLib.BUNGEE_CHANNEL, out.toByteArray());
    }

    public static boolean isServerConnected(String channel) {
        return CONNECTED_CHANNEL.contains(channel);
    }

}
