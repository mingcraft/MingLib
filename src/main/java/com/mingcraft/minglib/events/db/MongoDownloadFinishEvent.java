package com.mingcraft.minglib.events.db;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class MongoDownloadFinishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final String key;
    private final MongoCollection<Document> collection;
    private final List<Document> documents;
    private final UUID player;

    public MongoDownloadFinishEvent(String key, MongoCollection<Document> collection, List<Document> documents) {
        this.key = key;
        this.collection = collection;
        this.documents = documents;
        this.player = null;
    }

    public MongoDownloadFinishEvent(String key, MongoCollection<Document> collection, List<Document> documents, UUID player) {
        this.key = key;
        this.collection = collection;
        this.documents = documents;
        this.player = player;
    }

    public String getKey() {
        return key;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isPlayerDocument() {
        return player != null;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
