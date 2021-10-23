package com.mingcraft.minglib.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.UUID;

public class RealPlayer {

    private final Player player;
    private final String uuid;
    private final String texture;
    private final ItemStack head;

    public RealPlayer(Player player) {
        this.player = player;
        this.uuid = getUuidFromMCServer();
        this.texture = getTextureFromMCServer();
        this.head = getSkullFromMCServer();
    }

    private String getUuidFromMCServer() {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + this.player.getName());
            InputStreamReader reader = new InputStreamReader(url.openStream());
            return new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTextureFromMCServer() {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + this.uuid + "?unsigned=false");
            InputStreamReader reader1 = new InputStreamReader(url.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader1).getAsJsonObject().get("properties")
                    .getAsJsonArray().get(0).getAsJsonObject();
            return textureProperty.get("value").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ItemStack getSkullFromMCServer() {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        Field profileField;
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), this.player.getName());
        gameProfile.getProperties().put("textures", new Property("textures", this.texture));
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, gameProfile);
            itemStack.setItemMeta(meta);
            profileField.setAccessible(false);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ignored) {}

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public Player getPlayer() {
        return player;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTexture() {
        return texture;
    }

    public ItemStack getHead() {
        return head;
    }

}
