package com.mingcraft.minglib.db;

public abstract class PlayerData {

    public abstract Object getPlayerData(String uuid);

    public abstract boolean hasPlayerData(String uuid);

    public abstract void initPlayerData(String uuid);

}
