package com.deathgod7.multicurrency.utils;

import com.deathgod7.multicurrency.MultiCurrency;
import org.bukkit.Bukkit;
import redempt.redlib.commandmanager.Messages;

public final class ConsoleLogger {

    static String logPrefix = "[MultiCurrency] ";

    public enum logTypes {
        log,
        debug
    }

    public static boolean debugMode = MultiCurrency.getInstance().getMainConfig().debug;

    public static void severe(String msg, logTypes logType) {
        if (logType == logTypes.debug) {
            if (debugMode) { Bukkit.getLogger().severe(logPrefix + msg); }
        }
        else if (logType == logTypes.log) {
            Bukkit.getLogger().severe(logPrefix + msg);
        }
    }

    public static void warn(String msg, logTypes logType) {
        if (logType == logTypes.debug) {
            if (debugMode) { Bukkit.getLogger().warning(logPrefix + msg); }
        }
        else if (logType == logTypes.log) {
            Bukkit.getLogger().warning(logPrefix + msg);
        }
    }

    public static void info(String msg, logTypes logType) {
        if (logType == logTypes.debug) {
            if (debugMode) { Bukkit.getLogger().info(logPrefix + msg); }
        }
        else if (logType == logTypes.log) {
            Bukkit.getLogger().info(logPrefix + msg);
        }
    }

}
