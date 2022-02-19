package com.deathgod7.multicurrency.depends;

import com.deathgod7.multicurrency.depends.economy.VaultHook;
import org.bukkit.Bukkit;

public class EconomyManager {
    public static boolean haseco = false;
    public static boolean vault = false;

    public static boolean load() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            vault = true;
            VaultHook.load();
            haseco = true;
        }
        return haseco;
    }

    public static void unload() {
        if (vault) {
            VaultHook.unload();
        }
    }

    public static void reload() {
        if (vault) {
            VaultHook.reload();
        }
    }

}
