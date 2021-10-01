package com.mingcraft.minglib.db;

import com.google.api.client.util.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mingcraft.minglib.MingLib;
import com.mingcraft.minglib.events.db.MongoDownloadFinishEvent;
import com.mingcraft.minglib.events.db.MongoUploadFinishEvent;
import com.mingcraft.minglib.exceptions.db.MongoConnectionFailedException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

/**
 * MingCraft MongoDB API
 */
public class MongoDB {

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
        deleteOne(key, value);
        insertOne(document);
        callMongoUploadFinishEvent(key, collection, new ArrayList<>() {{add(document);}}, getPlayerKey(document));
    }

    /**
     * Update many documents by inquiring MongoCollection with key and value.
     * @param documents Documents that you want to update.
     * @param key The key inside the document to be updated.
     * @param value The value that matches the key.
     */
    public void updateMany(List<Document> documents, String key, Object value) {
        deleteMany(key, value);
        insertMany(documents);
        callMongoUploadFinishEvent(key, collection, documents, getPlayerKey(documents.get(0)));
    }

    /**
     * Update many documents by inquiring MongoCollection with key.
     * @param documents Documents that you want to update.
     * @param key The key inside the document to be updated.
     */
    public void updateMany(List<Document> documents, String key) {
        deleteMany(key);
        insertMany(documents);
        callMongoUploadFinishEvent(key, collection, documents, getPlayerKey(documents.get(0)));
    }

    /**
     * Update all documents in MongoCollection.
     * @param documents Documents that you want to update.
     */
    public void updateAll(List<Document> documents) {
        deleteAll();
        insertMany(documents);
        callMongoUploadFinishEvent(key, collection, documents, getPlayerKey(documents.get(0)));
    }

    /**
     * Insert one document to MongoCollection
     * @param document Document that you want to insert
     */
    public void insertOne(Document document) {
        collection.insertOne(document);
        callMongoUploadFinishEvent(key, collection, new ArrayList<>() {{add(document);}}, getPlayerKey(document));
    }

    /**
     * Insert many documents to MongoCollection
     * @param documents Documents that you want to insert
     */
    public void insertMany(List<Document> documents) {
        collection.insertMany(documents);
        callMongoUploadFinishEvent(key, collection, documents, getPlayerKey(documents.get(0)));
    }

    /**
     * Download one document from MongoCollection with key and value.
     * @param key The key inside the document to download.
     * @param value The value that matches the key.
     * @return Downloaded document.
     */
    public Document downloadOne(String key, Object value) {
        Document document = collection.find(Filters.eq(key, value)).first();
        if (document != null) {
            callMongoDownloadFinishEvent(this.key, collection, new ArrayList<>() {{add(document);}}, getPlayerKey(document));
        }
        return document;
    }

    /**
     * Download many documents from MongoCollection with key and value.
     * @param key The key inside the document to download.
     * @param value The value that matches the key.
     * @return Downloaded documents.
     */
    public List<Document> downloadMany(String key, Object value) {
        MongoCursor<Document> cursor = collection.find(Filters.eq(key, value)).iterator();
        List<Document> documents = new ArrayList<>();
        while (cursor.hasNext()) {
            documents.add(cursor.next());
        }
        if (documents.size() > 0) {
            callMongoDownloadFinishEvent(this.key, collection, documents, getPlayerKey(documents.get(0)));
        }
        return documents;
    }

    /**
     * Download many documents from MongoCollection with key.
     * @param key The key inside the document to download.
     * @return Downloaded documents.
     */
    public List<Document> downloadMany(String key) {
        MongoCursor<Document> cursor = collection.find(Filters.exists(key)).iterator();
        List<Document> documents = new ArrayList<>();
        while (cursor.hasNext()) {
            documents.add(cursor.next());
        }
        if (documents.size() > 0) {
            callMongoDownloadFinishEvent(this.key, collection, documents, getPlayerKey(documents.get(0)));
        }
        return documents;
    }

    /**
     * Download all documents from MongoCollection.
     * @return Downloaded documents.
     */
    public List<Document> downloadAll() {
        MongoCursor<Document> cursor = collection.find().iterator();
        List<Document> documents = new ArrayList<>();
        while (cursor.hasNext()) {
            documents.add(cursor.next());
        }
        if (documents.size() > 0) {
            callMongoDownloadFinishEvent(this.key, collection, documents, getPlayerKey(documents.get(0)));
        }
        return documents;
    }

    public void deleteOne(String key, Object value) {
        collection.deleteOne(Filters.eq(key, value));
    }

    public void deleteMany(String key, Object value) {
        collection.deleteMany(Filters.eq(key, value));
    }

    public void deleteMany(String key) {
        collection.deleteMany(Filters.exists(key));
    }

    public void deleteAll() {
        collection.drop();
    }

    public void uploadFile(File file) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(file);

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

    public static void registerMongoDB() {
        boolean useAtlas = MingLib.config.getBoolean("MongoDB.UseAtlas");
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

    private static void registerCollection(String key) {
        MongoDB mongo = new MongoDB(key);
        COLLECTION_MAP.put(key, mongo);
    }

    private static void clearCollections() {
        COLLECTION_MAP.clear();
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

    private static UUID getPlayerKey(Document document) {
        return UUID.fromString((String) document.get(KEY_PLAYER));
    }

    private static void callMongoUploadFinishEvent(String key, MongoCollection<Document> collection, List<Document> documents, @Nullable UUID player) {
        MongoUploadFinishEvent event = new MongoUploadFinishEvent(key, collection, documents, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    private static void callMongoDownloadFinishEvent(String key, MongoCollection<Document> collection, List<Document> documents, @Nullable UUID player) {
        MongoDownloadFinishEvent event = new MongoDownloadFinishEvent(key, collection, documents, player);
        Bukkit.getPluginManager().callEvent(event);
    }
}
