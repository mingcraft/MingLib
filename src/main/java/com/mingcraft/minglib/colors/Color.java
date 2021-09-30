package com.mingcraft.minglib.colors;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mingcraft color API <br>
 * <br>
 * Version 1.0.0 <br>
 *  - Add String colored Method <br>
 *  - Add String uncolored Method <br>
 *  - Add defined hex color <br>
 *
 *  <br>
 * @version 1.0.0
 * @author Lede
 */
public class Color {

    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");

    /**
     * Add hex color to String
     * @param message String with hex code
     * @return Colored String
     */
    public static String colored(String message) {
        Matcher matcher = HEX_PATTERN.matcher(ChatColor.translateAlternateColorCodes('&', message));
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(builder, ChatColor.of(matcher.group(1)).toString());
        }

        return matcher.appendTail(builder).toString();
    }

    /**
     * Add hex color to String[]
     * @param messages String[] with hex code
     * @return Colored String List
     */
    public static List<String> colored(String... messages) {
        return new ArrayList<String>(messages.length) {{
            for (String message : messages) {
                add(colored(message));
            }
        }};
    }

    /**
     * Add hex color to String Collection
     * @param messages String Collection with hex code
     * @return Colored String List
     */
    public static List<String> colored(Collection<String> messages) {
        return new ArrayList<String>(messages.size()) {{
            for (String message : messages) {
                add(colored(message));
            }
        }};
    }

    /**
     * Remove hex color from String
     * @param message Colored String
     * @return Uncolored String
     */
    public static String uncolored(String message) {
        return ChatColor.stripColor(message);
    }

    /**
     * Remove hex color from String[]
     * @param messages Colored String[]
     * @return Uncolored String List
     */
    public static List<String> uncolored(String... messages) {
        return new ArrayList<String>(messages.length) {{
            for (String message : messages) {
                add(uncolored(message));
            }
        }};
    }

    /**
     * Remove hex color from String Collection
     * @param messages Colored String Collection
     * @return Uncolored String List
     */
    public static List<String> uncolored(Collection<String> messages) {
        return new ArrayList<String>(messages.size()) {{
            for (String message : messages) {
                add(uncolored(message));
            }
        }};
    }

    /**
     * Minecraft formatting codes <br>
     * @see <a href="https://minecraft.fandom.com/wiki/Formatting_codes#Formatting_codes">Minecraft Formatting Codes</a>
     */
    public interface Format {
        String OBFUSCATED = "&k";
        String BOLD = "&l";
        String STRIKETHROUGH = "&m";
        String UNDERLINE = "&m";
        String italic = "&o";
        String RESET = "&r";
    }

    /**
     * Minecraft color codes
     * @see <a href="https://minecraft.fandom.com/wiki/Formatting_codes#Color_codes">Minecraft Color Codes</a>
     */
    public interface Vanilla {
        String BLACK = "&0";
        String DARK_BLUE = "&1";
        String DARK_GREEN = "&2";
        String DARK_AQUA = "&3";
        String DARK_RED = "&4";
        String DARK_PURPLE = "&5";
        String GOLD = "&6";
        String GRAY = "&7";
        String DARK_GRAY = "&8";
        String BLUE = "&9";
        String GREEN = "&a";
        String AQUA = "&b";
        String RED = "&c";
        String LIGHT_PURPLE = "&d";
        String YELLOW = "&e";
        String WHITE = "&f";
    }

    /**
     * Defined Hex Colors <br>
     * If you want other hex color, visit here
     * @see <a href="https://encycolorpedia.kr/">Encycolorpedia</a>
     */
    public interface Hex {
        String BLACK = "&#000000";
        String TULIP_NOIR = "&#392f31";
        String MARIE_ROUGE = "&#edacb1";
        String ROSE = "&#8d192b";
        String MAGENTA = "&#dc143c";
        String CORAL = "&#f29886";
        String MAROON = "&#800000";
        String RED = "&#ff0000";
        String ORANGE = "&#ff7f00";
        String APRICOT = "&#fbceb1";
        String BROWN = "&#964b00";
        String MANDARIN = "&#f89b00";
        String OCHER = "&#c68a12";
        String KHAKI = "&#8f784b";
        String YELLOW = "&#ffd400";
        String GOLD = "&#ffd700";
        String FORSYTHIA = "&#f7e600";
        String LIGHT_IVORY = "&#eee6c4";
        String IVORY = "&#ece6cc";
        String OLIVE = "&#808000";
        String BEIGE = "&#f5f5dc";
        String GRASS = "&#6a8518";
        String LIME = "&#bfff00";
        String LIGHT_GREEN = "&#81c147";
        String GREEN = "&#009900";
        String GREENNESS = "&#008000";
        String EMERALD = "&#008d62";
        String JADE = "&#83dcb7";
        String DARK_BLUE = "&#008080";
        String MARINE = "&#0099a4";
        String TURQUOISE = "&#005666";
        String SKY_BLUE = "&#50bcdf";
        String CYAN = "&#00a3d2";
        String CYAN_BLUE = "&#3e91b5";
        String CERULEAN_FLASH = "&#0096c6";
        String LIGHT_BLUE = "&#4aa8d8";
        String AQUAMARINE = "&#5e7e9b";
        String SHEFFIELD_STEEL = "&#437299";
        String BLUE = "&#0067a3";
        String PRUSSIAN_BLUE = "&#003458";
        String COBALT_BLUE = "&#00498c";
        String SEA_BLUE = "&#0080ff";
        String ULTRAMARINE = "&#464964";
        String WHITE = "&#ffffff";
        String GRAY = "&#808080";
        String SILVER = "&#c0c0c0";
        String LIGHT_PURPLE = "&#8977ad";
        String INDIGO = "&#000080";
        String BLUE_PURPLE = "&#6937a1";
        String PURPLE = "&#8b00ff";
        String AMETHYST = "&#660099";
        String CLARET = "&#ff00ff";
        String PINK = "&#ff3399";
    }

}
