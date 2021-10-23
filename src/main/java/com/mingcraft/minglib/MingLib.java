package com.mingcraft.minglib;

import com.mingcraft.minglib.db.MongoDB;
import com.mingcraft.minglib.player.PlayerListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MingLib extends JavaPlugin {

    public static FileConfiguration config;
    public static MingLib instance;

    @Override
    public void onEnable() {
        instance = this;
        registerConfig();
        MongoDB.registerMongoDB();
        new PlayerListener(this);
    }

    private void registerConfig() {
        saveDefaultConfig();
        config = getConfig();
    }

}
