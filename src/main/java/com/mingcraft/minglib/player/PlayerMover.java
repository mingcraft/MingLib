package com.mingcraft.minglib.player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mingcraft.minglib.MingLib;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerMover {

    private static final List<String> movedPlayer = new ArrayList<>();

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
        enablePlayerServerMoveState(player);
        MongoPlayer.unregisterPlayerWhenServerMove(player, channel);
    }

    public static void moveServer(Player player, String channel) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(channel);
        player.sendPluginMessage(MingLib.instance, MingLib.BUNGEE_CHANNEL, out.toByteArray());
    }

}
