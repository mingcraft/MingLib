package com.mingcraft.minglib.events.log;

import com.mingcraft.minglib.logs.Logger;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LoggerUnregisteredEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final String key;
    private final Logger logger;

    public LoggerUnregisteredEvent(String key, Logger logger) {
        this.key = key;
        this.logger = logger;
    }

    public String getKey() {
        return key;
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
