package com.mingcraft.minglib.player;

import com.mingcraft.minglib.db.MongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public abstract class PlayerData {

    public void download(RealPlayer player, MongoPlayer mongoPlayer) {
        Class<?> clazz = mongoPlayer.getDataClass();
        if (clazz == null) {
            return;
        }

        String uuid = player.getUuid();
        Document document = mongoPlayer.getCollection().find(Filters.eq("uuid", uuid)).first();
        if (document == null) {
            return;
        }

        mongoPlayer.getData().put(player.getUuid(), (PlayerData) MongoDB.toObject(document, clazz));
    }

    public void save(RealPlayer player, MongoPlayer mongoPlayer) {
        String uuid = player.getUuid();
        PlayerData data = mongoPlayer.getData().get(uuid);
        if (data == null) {
            return;
        }

        Document document = MongoDB.toDocument(data);
        if (document == null) {
            return;
        }

        MongoCollection<Document> collection = mongoPlayer.getCollection();
        collection.deleteOne(Filters.eq("uuid", uuid));
        collection.insertOne(document);
    }

    public void unload(RealPlayer player, MongoPlayer mongoPlayer) {
        mongoPlayer.getData().remove(player.getUuid());
    }

}
