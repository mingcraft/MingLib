package com.mingcraft.minglib.overlay;

import com.mingcraft.minglib.MingLib;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Overlay {

    public static void sendLoadingScreen(Player player, int duration) {
        PacketPlayOutGameStateChange packetPlayOutGameStateChange =
                new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.e, 0F);
        ((CraftPlayer) player).getHandle().b.sendPacket(packetPlayOutGameStateChange);
        Bukkit.getScheduler().scheduleSyncDelayedTask(MingLib.instance, player::closeInventory, duration);
    }

}
