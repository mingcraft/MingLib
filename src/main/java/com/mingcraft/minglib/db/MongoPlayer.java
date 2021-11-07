package com.mingcraft.minglib.db;

import com.mingcraft.minglib.player.RealPlayer;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MongoPlayer {

    private static final ExecutorService executor = Executors.newFixedThreadPool(12);
    private static final Map<MongoCollection<Document>, Map<String, PlayerData>> collectionMap = new HashMap<>();
    private static final Map<MongoCollection<Document>, Class<?>> classMap = new HashMap<>();

    public static void registerCollection(MongoCollection<Document> collection, Map<String, PlayerData> dataMap, Class<?> valueClass) {
        collectionMap.put(collection, dataMap);
        classMap.put(collection, valueClass);
    }

    public static void downloadPlayerData(RealPlayer player) {
        executor.execute(() ->
                collectionMap.forEach((key, value) -> {
                    Class<?> clazz = classMap.get(key);
                    download(player, key, value, clazz);
                })
        );
    }

    public static void savePlayerData(RealPlayer player) {
        executor.execute(() ->
                collectionMap.forEach((key, value) ->
                        save(player, key, value)
                )
        );
    }

    public static void unloadPlayerData(RealPlayer player) {
        executor.execute(() -> {
            for (Map<String, PlayerData> dataMap : collectionMap.values()) {
                dataMap.remove(player.getUuid());
            }
        });
    }

    public static void downloadPlayerData(RealPlayer player, MongoCollection<Document> collection) {
        executor.execute(() -> {
            Map<String, PlayerData> dataMap = collectionMap.get(collection);
            Class<?> clazz = classMap.get(collection);
            download(player, collection, dataMap, clazz);
        });
    }

    public static void savePlayerData(RealPlayer player, MongoCollection<Document> collection) {
        executor.execute(() -> {
            Map<String, PlayerData> dataMap = collectionMap.get(collection);
            save(player, collection, dataMap);
        });
    }

    public static void saveAndUnloadPlayerData(RealPlayer player) {
        executor.execute(() -> {
            collectionMap.forEach((key, value) ->
                    save(player, key, value)
            );

            for (Map<String, ? extends PlayerData> dataMap : collectionMap.values()) {
                dataMap.remove(player.getUuid());
            }
        });
    }

    private static void download(RealPlayer player, MongoCollection<Document> collection, Map<String, PlayerData> dataMap, Class<?> clazz) {
        try {
            Method method = clazz.getDeclaredMethod("download", RealPlayer.class, MongoCollection.class, Map.class, Class.class);
            method.invoke(clazz.getConstructor().newInstance(), player, collection, dataMap, clazz);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static void save(RealPlayer player, MongoCollection<Document> collection, Map<String, PlayerData> dataMap) {
        Class<?> clazz = classMap.get(collection);
        if (clazz == null) {
            return;
        }

        try {
            Method method = clazz.getDeclaredMethod("save", RealPlayer.class, MongoCollection.class, Map.class);
            method.invoke(clazz.getConstructor().newInstance(), player, collection, dataMap);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

}
