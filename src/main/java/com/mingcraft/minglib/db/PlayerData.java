package com.mingcraft.minglib.db;

public interface PlayerData {

    Object getPlayerData(String uuid);

    boolean hasPlayerData(String uuid);

    void initPlayerData(String uuid);

}
