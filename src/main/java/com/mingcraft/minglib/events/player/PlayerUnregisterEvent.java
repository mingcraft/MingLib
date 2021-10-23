package com.mingcraft.minglib.events.player;

import com.mingcraft.minglib.player.RealPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerUnregisterEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final RealPlayer realPlayer;

    public PlayerUnregisterEvent(RealPlayer realPlayer) {
        this.realPlayer = realPlayer;
    }

    public RealPlayer getRealPlayer() {
        return realPlayer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
