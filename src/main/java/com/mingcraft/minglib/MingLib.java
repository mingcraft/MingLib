package com.mingcraft.minglib;

import com.mingcraft.minglib.db.MongoDB;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class MingLib extends JavaPlugin {

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        registerConfig();
        MongoDB.registerMongoDB();

    }

    @Override
    public void onDisable() {

    }

    private void registerConfig() {
        saveDefaultConfig();
        config = getConfig();
    }
}
