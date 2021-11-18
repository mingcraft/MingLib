package com.mingcraft.minglib;

import com.mingcraft.minglib.db.MongoDB;
import com.mingcraft.minglib.player.MongoPlayer;
import com.mingcraft.minglib.player.PlayerListener;
import com.mingcraft.minglib.player.PlayerLoader;
import com.mingcraft.minglib.player.manager.MCServerRegisterPacketListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MingLib extends JavaPlugin {

    public static final String BUNGEE_CHANNEL = "BungeeCord";

    public static FileConfiguration config;
    public static MingLib instance;

    @Override
    public void onEnable() {
        registerInstance();
        registerConfig();
        registerBungeeChannel();
        registerListeners();
        MongoDB.registerMongoDB();
        new PlayerListener(this);
    }

    @Override
    public void onDisable() {
        MongoPlayer.shutdown();
        PlayerLoader.shutdown();
        unregisterBungeeChannel();
    }

    private void registerInstance() {
        instance = this;
    }

    private void registerConfig() {
        saveDefaultConfig();
        config = getConfig();
    }

    private void registerListeners() {
        new MCServerRegisterPacketListener(this);
    }

    private void registerBungeeChannel() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, BUNGEE_CHANNEL);
    }

    private void unregisterBungeeChannel() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, BUNGEE_CHANNEL);
    }

}
