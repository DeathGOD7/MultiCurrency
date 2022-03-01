package com.deathgod7.multicurrency.depends.economy;

import com.deathgod7.multicurrency.MultiCurrency;
import me.lokka30.treasury.api.economy.currency.Currency;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.*;

public class TreasuryManager {
    TreasuryHook treasuryHook = new TreasuryHook();

    public TreasuryHook getTreasuryHook() {
        return treasuryHook;
    }

    public static Set<Currency> econ = null;
    // public static Permission vaultPerm = null;
    public static Map<String, CurrencyTypes> ecoconfigs = MultiCurrency.getInstance().getCurrencyTypeManager().getAllCurrencyTypes();

    private static Currency convertToTreasury(CurrencyTypes currencyTypes){
        return new Treasury(currencyTypes);
    }

    public static void load() {

        for (String x : ecoconfigs.keySet()) {
            econ.add(convertToTreasury(ecoconfigs.get(x)));
        }

        if (MultiCurrency.getInstance().getMainConfig().disable_essentials) {
            if (Bukkit.getPluginManager().getPlugin("Treasury") == null){
                Collection<RegisteredServiceProvider<Economy>> registeredecons = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Vault")).getServer().getServicesManager().getRegistrations(Economy.class);
                for (RegisteredServiceProvider<Economy> eco : registeredecons) {
                    if (eco.getProvider().getName().equalsIgnoreCase("Essentials Economy")) {
                        MultiCurrency.getInstance().getServer().getServicesManager().unregister(eco.getProvider());
                    }
                }
            }
        }
    }

    public static void unload() {
        MultiCurrency.getInstance().getServer().getServicesManager().unregister(econ);
    }

    public static void reload() {
        unload();
        ecoconfigs =  MultiCurrency.getInstance().getCurrencyTypeManager().getAllCurrencyTypes();
        load();
    }

}
