package com.deathgod7.multicurrency.utils;

import org.bukkit.command.CommandSender;

public class SE7ENUtils {
    public static boolean hasDebugPerms(CommandSender cms) {
        if (cms.hasPermission("multicurrency.debug")) {
            return true;
        }
        return false;
    }
}
