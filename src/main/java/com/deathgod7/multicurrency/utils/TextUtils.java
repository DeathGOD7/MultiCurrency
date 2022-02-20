package com.deathgod7.multicurrency.utils;

import org.bukkit.ChatColor;

public class TextUtils {
    public static String ConvertTextColor (char symbol, String text){
        return ChatColor.translateAlternateColorCodes(symbol, text);
    }
}
