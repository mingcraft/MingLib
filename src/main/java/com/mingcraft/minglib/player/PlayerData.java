package com.mingcraft.minglib.player;

import com.mingcraft.minglib.db.MongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Map;

public abstract class PlayerData {

    public void download(RealPlayer player, MongoCollection<Document> collection, Map<String, PlayerData> dataMap, Class<?> clazz) {
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

    public void save(RealPlayer player, MongoCollection<Document> collection, Map<String, PlayerData> dataMap) {
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

    public void unload(RealPlayer player, MongoCollection<Document> collection, Map<String, PlayerData> dataMap) {
        dataMap.remove(player.getUuid());
    }

}
