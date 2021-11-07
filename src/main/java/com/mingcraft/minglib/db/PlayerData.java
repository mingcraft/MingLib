package com.mingcraft.minglib.db;

public abstract class PlayerData {

    abstract Object getPlayerData(String uuid);

    abstract boolean hasPlayerData(String uuid);

    abstract void initPlayerData(String uuid);

}
