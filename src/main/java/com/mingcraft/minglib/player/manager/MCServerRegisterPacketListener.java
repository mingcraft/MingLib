package com.mingcraft.minglib.player.manager;

import com.mingcraft.asmconnector.asm.events.packetevents.MCServerRegisterPacketEvent;
import com.mingcraft.asteapacketmanager.packet.Protocol;
import com.mingcraft.minglib.MingLib;
import com.mingcraft.minglib.colors.Color;
import com.mingcraft.minglib.player.PlayerMover;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class MCServerRegisterPacketListener implements Listener {

    public MCServerRegisterPacketListener(MingLib plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMCServerRegister(MCServerRegisterPacketEvent event) {
        if (event.getType() == Protocol.CONNECTOR_REGISTER) {
            PlayerMover.setConnectedChannel(event.getServerList());
            if (event.getRegisterType().toLowerCase().startsWith("register"))
                Bukkit.getLogger().log(Level.INFO, Color.colored("&bASM에 마인크래프트 서버 연결이 감지되었습니다. 서버 목록을 업데이트 합니다."));
            else
                Bukkit.getLogger().log(Level.INFO, Color.colored("&eASM에 마인크래프트 서버 연결 해제가 감지되었습니다. 서버 목록을 업데이트 합니다."));
            Bukkit.getLogger().log(Level.INFO, Color.colored("&b" + event.getServerList()));
        }
    }


}
