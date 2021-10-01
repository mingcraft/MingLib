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

import java.util.*;

public class MongoDB {

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

    public static MongoDB getMongoDB(String key) {
        if (!hasConnection(key)) {
            registerCollection(key);
        }
        return COLLECTION_MAP.get(key);
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public String getKey() {
        return key;
    }

    public void updateOne(Document document, String key, Object value) {
        deleteOne(key, value);
        insertOne(document);
    }

    public void updateMany(List<Document> documents, String key, Object value) {
        deleteMany(key, value);
        insertMany(documents);
    }

    public void updateMany(List<Document> documents, String key) {
        deleteMany(key);
        insertMany(documents);
    }

    public void updateAll(List<Document> documents) {
        deleteAll();
        insertMany(documents);
    }

    public void insertOne(Document document) {
        collection.insertOne(document);
    }

    public void insertMany(List<Document> documents) {
        collection.insertMany(documents);
    }

    public Document downloadOne(String key, Object value) {
        return collection.find(Filters.eq(key, value)).first();
    }

    public Set<Document> downloadMany(String key, Object value) {
        MongoCursor<Document> cursor = collection.find(Filters.eq(key, value)).iterator();
        Set<Document> documents = new HashSet<>();
        while (cursor.hasNext()) {
            documents.add(cursor.next());
        }
        return documents;
    }

    public Set<Document> downloadMany(String key) {
        MongoCursor<Document> cursor = collection.find(Filters.exists(key)).iterator();
        Set<Document> documents = new HashSet<>();
        while (cursor.hasNext()) {
            documents.add(cursor.next());
        }
        return documents;
    }

    public Set<Document> downloadAll() {
        MongoCursor<Document> cursor = collection.find().iterator();
        Set<Document> documents = new HashSet<>();
        while (cursor.hasNext()) {
            documents.add(cursor.next());
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

    private static boolean hasConnection(String key) {
        return COLLECTION_MAP.containsKey(key);
    }
}
