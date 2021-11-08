package com.mingcraft.minglib.player;

import com.mingcraft.minglib.colors.Color;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class MongoPlayer {

    private static final ExecutorService executor = Executors.newFixedThreadPool(12);
    private static final Map<MongoCollection<Document>, Map<String, PlayerData>> collectionMap = new HashMap<>();
    private static final Map<MongoCollection<Document>, Class<?>> classMap = new HashMap<>();

    public static void registerCollection(MongoCollection<Document> collection, Map<String, PlayerData> dataMap, Class<?> valueClass) {
        collectionMap.put(collection, dataMap);
        classMap.put(collection, valueClass);
        Bukkit.getLogger().log(Level.INFO, Color.colored(
                "&6[MongoPlayer] &a" + collection.getNamespace().getCollectionName() +
                        " &eCollection Bind."
        ));
        Bukkit.getLogger().log(Level.INFO, Color.colored(
                "&6[MongoPlayer] &a" + valueClass.getName() + " &eClass Bind."
        ));
    }

    public static void shutdown() {
        executor.shutdown();
    }

    public static void downloadPlayerData(RealPlayer player) {
        executor.execute(() -> {
            AtomicInteger count = new AtomicInteger();
            collectionMap.forEach((key, value) -> {
                download(player, key, value);
                count.getAndIncrement();
            });
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &a" + count +"&e of &3" +
                            player.getPlayer().getName() + "&e's Data Download Complete."
            ));
        });
    }

    public static void savePlayerData(RealPlayer player) {
        executor.execute(() -> {
            AtomicInteger count = new AtomicInteger();
            collectionMap.forEach((key, value) -> {
                save(player, key, value);
                count.getAndIncrement();
            });
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &a" + count +"&e of &3" +
                            player.getPlayer().getName() + "&e's Data Save Complete."
            ));
        });
    }

    public static void unloadPlayerData(RealPlayer player) {
        executor.execute(() -> {
            AtomicInteger count = new AtomicInteger();
            collectionMap.forEach((key, value) -> {
                unload(player, key, value);
                count.getAndIncrement();
            });
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &a" + count +"&e of &3" +
                            player.getPlayer().getName() + "&e's Data Unload Complete."
            ));
        });
    }

    public static void downloadPlayerData(RealPlayer player, MongoCollection<Document> collection) {
        executor.execute(() -> {
            Map<String, PlayerData> dataMap = collectionMap.get(collection);
            download(player, collection, dataMap);
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &3" + player.getPlayer().getName() + "&e's &b" +
                            collection.getNamespace().getCollectionName() + " &eData Download Complete."
            ));
        });
    }

    public static void savePlayerData(RealPlayer player, MongoCollection<Document> collection) {
        executor.execute(() -> {
            Map<String, PlayerData> dataMap = collectionMap.get(collection);
            save(player, collection, dataMap);
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &3" + player.getPlayer().getName() + "&e's &b" +
                            collection.getNamespace().getCollectionName() + " &eData Save Complete."
            ));
        });
    }

    public static void unloadPlayerData(RealPlayer player, MongoCollection<Document> collection) {
        executor.execute(() -> {
            Map<String, PlayerData> dataMap = collectionMap.get(collection);
            unload(player, collection, dataMap);
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &3" + player.getPlayer().getName() + "&e's &b" +
                            collection.getNamespace().getCollectionName() + " &eData Unload Complete."
            ));
        });
    }

    public static void saveAndUnloadPlayerData(RealPlayer player) {
        executor.execute(() -> {
            AtomicInteger count = new AtomicInteger();
            collectionMap.forEach((key, value) -> {
                save(player, key, value);
                unload(player, key, value);
                count.getAndIncrement();
            });
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &a" + count +"&e of &3" + player.getPlayer().getName() +
                            "&e's Data Save/Unload Complete."
            ));
        });
    }

    private static void download(RealPlayer player, MongoCollection<Document> collection, Map<String, PlayerData> dataMap) {
        Class<?> clazz = classMap.get(collection);
        if (clazz == null) {
            return;
        }

        try {
            Method method = clazz.getMethod("download", RealPlayer.class, MongoCollection.class, Map.class, Class.class);
            method.invoke(clazz.getConstructor().newInstance(), player, collection, dataMap, clazz);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            else
                e.printStackTrace();
        }
    }

    private static void save(RealPlayer player, MongoCollection<Document> collection, Map<String, PlayerData> dataMap) {
        Class<?> clazz = classMap.get(collection);
        if (clazz == null) {
            return;
        }

        try {
            Method method = clazz.getMethod("save", RealPlayer.class, MongoCollection.class, Map.class);
            method.invoke(clazz.getConstructor().newInstance(), player, collection, dataMap);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            else
                e.printStackTrace();
        }
    }

    private static void unload(RealPlayer player, MongoCollection<Document> collection, Map<String, PlayerData> dataMap) {
        Class<?> clazz = classMap.get(collection);
        if (clazz == null) {
            return;
        }

        try {
            Method method = clazz.getMethod("unload", RealPlayer.class, MongoCollection.class, Map.class);
            method.invoke(clazz.getConstructor().newInstance(), player, collection, dataMap);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            else
                e.printStackTrace();
        }
    }

}
