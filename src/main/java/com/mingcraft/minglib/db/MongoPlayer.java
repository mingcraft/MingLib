package com.mingcraft.minglib.db;

import com.mingcraft.minglib.player.RealPlayer;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

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
                collectionMap.forEach((key, value) ->
                        download(player, key, value)
                )
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
            download(player, collection, dataMap);
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

    private static void download(RealPlayer player, MongoCollection<Document> collection, Map<String, PlayerData> dataMap) {
        Class<?> clazz = classMap.get(collection);
        if (clazz == null) {
            return;
        }

        String uuid = player.getUuid();
        Document document = collection.find(Filters.eq("uuid", uuid)).first();
        if (document == null) {
            return;
        }

        dataMap.put(player.getUuid(), (PlayerData) MongoDB.toObject(document, clazz));
    }

    private static void save(RealPlayer player, MongoCollection<Document> collection, Map<String, PlayerData> dataMap) {
        String uuid = player.getUuid();
        PlayerData data = dataMap.get(uuid);
        if (data == null) {
            return;
        }

        Document document = MongoDB.toDocument(data);
        if (document == null) {
            return;
        }

        collection.deleteOne(Filters.eq("uuid", uuid));
        collection.insertOne(document);
    }

}
