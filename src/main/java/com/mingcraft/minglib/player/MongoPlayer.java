package com.mingcraft.minglib.player;

import com.mingcraft.minglib.MingLib;
import com.mingcraft.minglib.colors.Color;
import com.mingcraft.minglib.db.MongoDB;
import com.mingcraft.minglib.events.player.PlayerRegisterEvent;
import com.mingcraft.minglib.events.player.PlayerUnregisterEvent;
import com.mingcraft.minglib.exceptions.player.UnregisteredMongoPlayerException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class MongoPlayer {

    private final MongoCollection<Document> collection;
    private final Map<String, PlayerData> data;
    private final Class<? extends PlayerData> clazz;

    public MongoPlayer(MongoCollection<Document> collection, Map<String, PlayerData> data, Class<? extends PlayerData> clazz) {
        this.collection = collection;
        this.data = data;
        this.clazz = clazz;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public Map<String, PlayerData> getData() {
        return data;
    }

    public Class<?> getDataClass() {
        return clazz;
    }

    private static final ExecutorService executor = Executors.newFixedThreadPool(12);
    private static final Map<String, MongoPlayer> mongoPlayerMap = new ConcurrentHashMap<>();

    public static void registerMongoPlayer(String key, Class<? extends PlayerData> valueClass) {
        MongoCollection<Document> collection = MongoDB.getMongoDB(key).getCollection();
        Map<String, PlayerData> data = new ConcurrentHashMap<>();
        MongoPlayer mongoPlayer = new MongoPlayer(collection, data, valueClass);
        mongoPlayerMap.put(key, mongoPlayer);

        Bukkit.getLogger().log(Level.INFO, Color.colored(
                "&6[MongoPlayer] &a" + key +
                        " &eCollection Bind."
        ));
        Bukkit.getLogger().log(Level.INFO, Color.colored(
                "&6[MongoPlayer] &a" + valueClass.getName() + " &eClass Bind."
        ));
    }

    public static void shutdown() {
        executor.shutdown();
    }

    public static MongoPlayer getMongoPlayer(String key) {
        MongoPlayer mongoPlayer = mongoPlayerMap.get(key);
        if (mongoPlayer != null) {
            return mongoPlayer;
        }
        throw new UnregisteredMongoPlayerException("등록되지 않은 MongoPlayer 입니다. MongoPlayer 를 불러올 수 없습니다. [ key = " + key + " ]");
    }

    public static MongoCollection<Document> getCollection(String key) {
        MongoPlayer mongoPlayer = mongoPlayerMap.get(key);
        if (mongoPlayer != null) {
            return mongoPlayer.collection;
        }
        throw new UnregisteredMongoPlayerException("등록되지 않은 MongoPlayer 입니다. MongoCollection 을 불러올 수 없습니다. [ key = " + key + " ]");
    }

    public static Map<String, PlayerData> getDataMap(String key) {
        MongoPlayer mongoPlayer = mongoPlayerMap.get(key);
        if (mongoPlayer != null) {
            return mongoPlayer.data;
        }
        throw new UnregisteredMongoPlayerException("등록되지 않은 MongoPlayer 입니다. Data 를 불러올 수 없습니다. [ key = " + key + " ]");
    }

    public static Class<?> getDataClass(String key) {
        MongoPlayer mongoPlayer = mongoPlayerMap.get(key);
        if (mongoPlayer != null) {
            return mongoPlayer.clazz;
        }
        throw new UnregisteredMongoPlayerException("등록되지 않은 MongoPlayer 입니다. DataClass 를 불러올 수 없습니다. [ key = " + key + " ]");
    }

    public static PlayerData getPlayerData(String key, String uuid) {
        MongoPlayer mongoPlayer = mongoPlayerMap.get(key);
        if (mongoPlayer != null) {
            return mongoPlayer.data.get(uuid);
        }
        throw new UnregisteredMongoPlayerException("등록되지 않은 MongoPlayer 입니다. Data 를 불러올 수 없습니다. [ key = " + key + " ]");
    }

    public static void registerPlayer(Player player) {
        executor.execute(() -> {
            RealPlayer realPlayer = new RealPlayer(player);
            PlayerLoader.getPlayerMap().put(player.getName(), realPlayer);

            MongoPlayer.downloadPlayerData(realPlayer);

            Bukkit.getScheduler().runTask(MingLib.instance, () -> {
                PlayerRegisterEvent event = new PlayerRegisterEvent(realPlayer);
                Bukkit.getPluginManager().callEvent(event);
            });
        });
    }

    public static void unregisterPlayer(Player player) {
        executor.execute(() -> {
            RealPlayer realPlayer = PlayerLoader.getPlayerMap().get(player.getName());
            MongoPlayer.saveAndUnloadPlayerData(realPlayer);
            PlayerLoader.getPlayerMap().remove(player.getName());

            Bukkit.getScheduler().runTask(MingLib.instance, () -> {
                PlayerUnregisterEvent event = new PlayerUnregisterEvent(realPlayer);
                Bukkit.getPluginManager().callEvent(event);
            });
        });
    }

    public static void downloadPlayerData(RealPlayer player) {
        executor.execute(() -> {
            AtomicInteger count = new AtomicInteger();
            mongoPlayerMap.forEach((key, value) -> {
                download(player, value);
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
            mongoPlayerMap.forEach((key, value) -> {
                save(player, value);
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
            mongoPlayerMap.forEach((key, value) -> {
                unload(player, value);
                count.getAndIncrement();
            });
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &a" + count +"&e of &3" +
                            player.getPlayer().getName() + "&e's Data Unload Complete."
            ));
        });
    }

    public static void downloadPlayerData(RealPlayer player, String key) {
        executor.execute(() -> {
            MongoPlayer mongoPlayer = mongoPlayerMap.get(key);
            download(player, mongoPlayer);
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &3" + player.getPlayer().getName() + "&e's &b" +
                            key + " &eData Download Complete."
            ));
        });
    }

    public static void savePlayerData(RealPlayer player, String key) {
        executor.execute(() -> {
            MongoPlayer mongoPlayer = mongoPlayerMap.get(key);
            save(player, mongoPlayer);
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &3" + player.getPlayer().getName() + "&e's &b" +
                            key + " &eData Save Complete."
            ));
        });
    }

    public static void unloadPlayerData(RealPlayer player, String key) {
        executor.execute(() -> {
            MongoPlayer mongoPlayer = mongoPlayerMap.get(key);
            unload(player, mongoPlayer);
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &3" + player.getPlayer().getName() + "&e's &b" +
                            key + " &eData Unload Complete."
            ));
        });
    }

    public static void saveAndUnloadPlayerData(RealPlayer player) {
        executor.execute(() -> {
            AtomicInteger count = new AtomicInteger();
            mongoPlayerMap.forEach((key, value) -> {
                save(player, value);
                unload(player, value);
                count.getAndIncrement();
            });
            Bukkit.getLogger().log(Level.INFO, Color.colored(
                    "&6[MongoPlayer] &a" + count +"&e of &3" + player.getPlayer().getName() +
                            "&e's Data Save/Unload Complete."
            ));
        });
    }

    private static void download(RealPlayer player, MongoPlayer mongoPlayer) {
        try {
            Class<?> clazz = mongoPlayer.getDataClass();
            Method method = clazz.getMethod("download", RealPlayer.class, MongoPlayer.class);
            method.invoke(clazz.getConstructor().newInstance(), player, mongoPlayer);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            else
                e.printStackTrace();
        }
    }

    private static void save(RealPlayer player, MongoPlayer mongoPlayer) {
        try {
            Class<?> clazz = mongoPlayer.getDataClass();
            Method method = clazz.getMethod("save", RealPlayer.class, MongoPlayer.class);
            method.invoke(clazz.getConstructor().newInstance(), player, mongoPlayer);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            else
                e.printStackTrace();
        }
    }

    private static void unload(RealPlayer player, MongoPlayer mongoPlayer) {
        try {
            Class<?> clazz = mongoPlayer.getDataClass();
            Method method = clazz.getMethod("unload", RealPlayer.class, MongoPlayer.class);
            method.invoke(clazz.getConstructor().newInstance(), player, mongoPlayer);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            if (e.getCause() != null)
                e.getCause().printStackTrace();
            else
                e.printStackTrace();
        }
    }

}
