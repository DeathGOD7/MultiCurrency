package com.deathgod7.multicurrency.utils;

import org.bukkit.command.CommandSender;

public class SE7ENUtils {
    public static boolean hasDebugPerms(CommandSender cms) {
        return cms.hasPermission("multicurrency.debug");
    }

    public static boolean hasSilentPerms(CommandSender cms) {
        return cms.hasPermission("multicurrency.use.silent");
    }

    public static boolean isPlayer(CommandSender cms) {
        return !cms.getName().equalsIgnoreCase("CONSOLE");
    }
}
