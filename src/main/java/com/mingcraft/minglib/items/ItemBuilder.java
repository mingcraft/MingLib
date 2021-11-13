package com.mingcraft.minglib.items;

import com.mingcraft.minglib.colors.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder() {
        init(new ItemStack(Material.STONE));
    }

    public ItemBuilder(ItemStack itemStack) {
        init(new ItemStack(itemStack));
    }

    public ItemBuilder(Material type) {
        init(new ItemStack(type));
    }

    public ItemBuilder(Material type, int amount) {
        init(new ItemStack(type, amount));
    }

    private void init(ItemStack itemStack) {
        setItemStack(itemStack);
        setItemStack(itemStack.getItemMeta());
    }

    public ItemBuilder setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public ItemBuilder setItemStack(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
        return this;
    }

    public ItemBuilder setType(Material type) {
        itemStack.setType(type);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(Math.max(amount, 1));
        return this;
    }

    public ItemBuilder setDisplayName(String name) {
        itemMeta.setDisplayName(Color.colored(name));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(Color.colored(lore));
        return this;
    }

    public ItemBuilder insertLore(String lore, int line) {
        List<String> lores = getLore();
        List<String> newLores = new ArrayList<>();
        int size = lores.size();
        int lineNumber = getLineNumber(size, line);
        for (int i = 0; i < size; i++) {
            if (i == lineNumber) {
                newLores.add(Color.colored(lore));
            }
            newLores.add(lores.get(i));
        }
        itemMeta.setLore(newLores);
        return this;
    }

    public ItemBuilder extractLore(int line) {
        List<String> lores = getLore();
        List<String> newLores = new ArrayList<>();
        int size = lores.size();
        int lineNumber = getLineNumber(size, line);
        for (int i = 0; i < size; i++) {
            if (i != lineNumber)
                newLores.add(lores.get(i));
        }
        itemMeta.setLore(newLores);
        return this;
    }

    public ItemBuilder appendLore(List<String> lore) {
        List<String> lores = getLore();
        lores.addAll(Color.colored(lore));
        itemMeta.setLore(lores);
        return this;
    }

    public ItemBuilder appendLore(String lore) {
        List<String> lores = getLore();
        lores.add(Color.colored(lore));
        itemMeta.setLore(lores);
        return this;
    }

    public ItemBuilder clearLore() {
        itemMeta.setLore(new ArrayList<>());
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchant, int level) {
        itemMeta.addEnchant(enchant, level, true);
        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchant) {
        itemMeta.removeEnchant(enchant);
        return this;
    }

    public ItemBuilder setEnchantLevel(Enchantment enchant, int level) {
        if (itemMeta.hasEnchant(enchant)) {
            removeEnchant(enchant);
        }
        addEnchant(enchant, level);
        return this;
    }

    public ItemBuilder enableItemFlag(ItemFlag flag) {
        itemMeta.addItemFlags(flag);
        return this;
    }

    public ItemBuilder disableItemFlag(ItemFlag flag) {
        itemMeta.removeItemFlags(flag);
        return this;
    }

    public ItemBuilder enableUnbreakable() {
        itemMeta.setUnbreakable(true);
        return this;
    }

    public ItemBuilder disableUnbreakable() {
        itemMeta.setUnbreakable(false);
        return this;
    }

    public ItemBuilder setCustomModelData(int customModelData) {
        itemMeta.setCustomModelData(customModelData);
        return this;
    }

    public ItemBuilder removeCustomModelData() {
        itemMeta.setCustomModelData(0);
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        if (itemMeta instanceof Damageable)
            ((Damageable) itemMeta).setDamage(durability);
        return this;
    }

    public ItemBuilder repair() {
        if (itemMeta instanceof Damageable)
            ((Damageable) itemMeta).setDamage(0);
        return this;
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        itemMeta.addAttributeModifier(attribute, modifier);
        return this;
    }

    public ItemBuilder removeAttributeModifier(Attribute attribute) {
        itemMeta.removeAttributeModifier(attribute);
        return this;
    }

    public ItemBuilder setLeatherArmorColor(int red, int green, int blue) {
        if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(org.bukkit.Color.fromRGB(red, green, blue));
        }
        return this;
    }

    public ItemBuilder setLeatherArmorColor(org.bukkit.Color color) {
        if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(color);
        }
        return this;
    }

    public ItemMeta getItemMeta() {
        return itemMeta;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private List<String> getLore() {
        return (itemMeta.getLore() == null) ? new ArrayList<>() : itemMeta.getLore();
    }

    private int getLineNumber(int size, int line) {
        if (line > size) return size - 1;
        if (line < 1) return 0;
        return line - 1;
    }
}
