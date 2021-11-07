package com.mingcraft.minglib.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mingcraft.minglib.MingLib;
import com.mingcraft.minglib.exceptions.db.MongoConnectionFailedException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MingCraft MongoDB API
 */
public class MongoDB {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    private static final String KEY_PLAYER = "uuid";
    private static final Gson gson = new GsonBuilder().create();

    private static final Map<String, MongoDB> COLLECTION_MAP = new HashMap<>();
    private static MongoClient client;
    private static MongoDatabase database;

    private final String key;
    private MongoCollection<Document> collection;

    private MongoDB(String key) {
        this.key = key;
        setupCollection();
    }

    /**
     * Get MongoDB with key
     * @param key MongoCollection key
     * @return MongoDB
     */
    public static MongoDB getMongoDB(String key) {
        if (!hasConnection(key)) {
            registerCollection(key);
        }
        return COLLECTION_MAP.get(key);
    }

    /**
     * Remove the collection with the key as the name from the connected collection list.
     * @param key MongoCollection key
     */
    public static void disconnect(String key) {
        if (hasConnection(key)) {
            getMongoDB(key).disconnect();
        }
        else {
            throw new MongoConnectionFailedException("Could not find the collection with key.");
        }
    }

    /**
     * Get MongoClient
     * @return MongoClient
     */
    public MongoClient getClient() {
        return client;
    }

    /**
     * Get MongoDatabase
     * @return MongoDatabase
     */
    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Get MongoCollection
     * @return MongoCollection
     */
    public MongoCollection<Document> getCollection() {
        return collection;
    }

    /**
     * Get key
     * @return Key, MongoCollection name
     */
    public String getKey() {
        return key;
    }

    /**
     * Update one document by inquiring MongoCollection with key and value.
     * @param document Document that you want to update.
     * @param key The key inside the document to be updated.
     * @param value The value that matches the key.
     */
    public void updateOne(Document document, String key, Object value) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            collection.deleteOne(Filters.eq(key, value));
            collection.insertOne(document);
        });
    }

    /**
     * Update many documents by inquiring MongoCollection with key and value.
     * @param documents Documents that you want to update.
     * @param key The key inside the document to be updated.
     * @param value The value that matches the key.
     */
    public void updateMany(List<Document> documents, String key, Object value) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            collection.deleteMany(Filters.eq(key, value));
            collection.insertMany(documents);
        });
    }

    /**
     * Update many documents by inquiring MongoCollection with key.
     * @param documents Documents that you want to update.
     * @param key The key inside the document to be updated.
     */
    public void updateMany(List<Document> documents, String key) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            collection.deleteMany(Filters.exists(key));
            collection.insertMany(documents);
        });
    }

    /**
     * Update all documents in MongoCollection.
     * @param documents Documents that you want to update.
     */
    public void updateAll(List<Document> documents) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            collection.drop();
            collection.insertMany(documents);
        });
    }

    /**
     * Insert one document to MongoCollection
     * @param document Document that you want to insert
     */
    public void insertOne(Document document) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            collection.insertOne(document);
        });
    }

    /**
     * Insert many documents to MongoCollection
     * @param documents Documents that you want to insert
     */
    public void insertMany(List<Document> documents) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            collection.insertMany(documents);
        });
    }

    /**
     * Download one document from MongoCollection with key and value.
     * @param key The key inside the document to download.
     * @param value The value that matches the key.
     * @return Downloaded document.
     */
    @Nullable
    public Document downloadOne(String key, Object value) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            return executor.submit(new DownloadOne(collection, key, value)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private record DownloadOne(
            MongoCollection<Document> collection,
            String key,
            Object value
    ) implements Callable<Document> {

        @Override
        public Document call() {
            return collection.find(Filters.eq(key, value)).first();
        }

    }

    /**
     * Download many documents from MongoCollection with key and value.
     * @param key The key inside the document to download.
     * @param value The value that matches the key.
     * @return Downloaded documents.
     */
    @Nullable
    public List<Document> downloadMany(String key, Object value) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            return executor.submit(new DownloadMany(collection, key, value)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private record DownloadMany(
        MongoCollection<Document> collection,
        String key,
        Object value
    ) implements Callable<List<Document>> {

        @Override
        public List<Document> call() {
            MongoCursor<Document> cursor = collection.find(Filters.eq(key, value)).iterator();
            List<Document> documents = new ArrayList<>();
            while (cursor.hasNext()) {
                documents.add(cursor.next());
            }
            return documents;
        }

    }

    /**
     * Download many documents from MongoCollection with key.
     * @param key The key inside the document to download.
     * @return Downloaded documents.
     */
    @Nullable
    public List<Document> downloadMany(String key) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            return executor.submit(new DownloadMany2(collection, key)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private record DownloadMany2(
        MongoCollection<Document> collection,
        String key
    ) implements Callable<List<Document>> {

        @Override
        public List<Document> call() {
            MongoCursor<Document> cursor = collection.find(Filters.exists(key)).iterator();
            List<Document> documents = new ArrayList<>();
            while (cursor.hasNext()) {
                documents.add(cursor.next());
            }
            return documents;
        }

    }

    /**
     * Download all documents from MongoCollection.
     * @return Downloaded documents.
     */
    @Nullable
    public List<Document> downloadAll() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            return executor.submit(new DownloadAll(collection)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private record DownloadAll(
        MongoCollection<Document> collection
    ) implements Callable<List<Document>> {

        @Override
        public List<Document> call() {
            MongoCursor<Document> cursor = collection.find().iterator();
            List<Document> documents = new ArrayList<>();
            while (cursor.hasNext()) {
                documents.add(cursor.next());
            }
            return documents;
        }

    }

    public void deleteOne(String key, Object value) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> collection.deleteOne(Filters.eq(key, value)));
    }

    public void deleteMany(String key, Object value) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> collection.deleteMany(Filters.eq(key, value)));
    }

    public void deleteMany(String key) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> collection.deleteMany(Filters.exists(key)));
    }

    public void deleteAll() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> collection.drop());
    }

    public static String toJson(Document document) {
        return document.toJson();
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static Document toDocument(String json) {
        return Document.parse(json);
    }

    public static Document toDocument(Object object) {
        return Document.parse(gson.toJson(object));
    }

    public static Object toObject(String json, Class<?> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static Object toObject(Document document, Class<?> clazz) {
        return gson.fromJson(document.toJson(), clazz);
    }

    public static Set<String> getKeySet() {
        return COLLECTION_MAP.keySet();
    }

    public static String toBase64(Object obj) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(obj);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Object incode error.", e);
        }
    }

    public static Object fromBase64(String base64) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (ClassNotFoundException | IOException e) {
            throw new IOException("Object decode error.", e);
        }
    }

    public static void registerMongoDB() {
        boolean useAtlas = MingLib.config.getBoolean("MongoDB.UseAtlas");
        Bukkit.getConsoleSender().sendMessage("[MingLib] Use Atlas : " + useAtlas);
        try {
            if (useAtlas) {
                registerWithAtlas();
            }
            else {
                registerWithIdPort();
            }
            clearCollections();
        } catch (Exception ex) {
            throw new MongoConnectionFailedException("Mongo DB could not be connected. Check ip and port in config.yml");
        }

    }

    private static void registerWithIdPort() {
        String ip = MingLib.config.getString("MongoDB.Ip");
        int port = MingLib.config.getInt("MongoDB.Port");
        setupClient(ip, port);
        setupDatabase();
    }

    private static void registerWithAtlas() {
        String atlasString = MingLib.config.getString("MongoDB.AtlasConnectionString");
        setupClient(atlasString);
        setupDatabase();
    }

    private static void setupClient(String ip, int port) {
        client = MongoClients.create(MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(
                        List.of(new ServerAddress(ip, port))
                )).build());
    }

    private static void setupClient(String atlasString) {
        ConnectionString connectionString = new ConnectionString(atlasString);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        client = MongoClients.create(settings);
    }

    private static void setupDatabase() {
        database = client.getDatabase(Objects.requireNonNull(MingLib.config.getString("MongoDB.Database")));
    }

    private static void registerCollection(String key) {
        MongoDB mongo = new MongoDB(key);
        COLLECTION_MAP.put(key, mongo);
    }

    private static void clearCollections() {
        COLLECTION_MAP.clear();
    }

    private void setupCollection() {
        collection = database.getCollection(key);
    }

    private void disconnect() {
        COLLECTION_MAP.remove(key);
    }

    public static boolean hasConnection(String key) {
        return COLLECTION_MAP.containsKey(key);
    }

    private static boolean hasPlayerKey(Document document) {
        return document.containsKey(KEY_PLAYER);
    }

    @Nullable
    private static UUID getPlayerKey(Document document) {
        String name = (String) document.get(KEY_PLAYER);
        if (name != null)
            return UUID.fromString(name);
        return null;
    }

}
