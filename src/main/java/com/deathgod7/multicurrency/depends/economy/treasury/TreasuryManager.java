package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import me.lokka30.treasury.api.economy.currency.Currency;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.*;

public class TreasuryManager {
    MultiCurrency instance;

    final TreasuryAccountManager treasuryAccountmanager;
    public final TreasuryAccountManager getTreasuryAccountmanager() {
        return treasuryAccountmanager;
    }

    final TreasuryHook treasuryHook;
    public final TreasuryHook getTreasuryHook() {
        return treasuryHook;
    }

    public TreasuryManager (MultiCurrency instance){
        this.instance = instance;
        treasuryAccountmanager = new TreasuryAccountManager(instance);
        treasuryHook = new TreasuryHook(instance);
        load();
    }

    public static HashMap<String, Currency> treasuryCurrency = new HashMap<>();
    public static HashMap<String, CurrencyType> currencyTypes = MultiCurrency.getInstance().getCurrencyTypeManager().getAllCurrencyTypes();

    private static Currency convertToTreasury(CurrencyType currencyType){
        return new TreasuryCurrency(currencyType);
    }

    public static void load() {

        for (String x : currencyTypes.keySet()) {
            treasuryCurrency.put(x, convertToTreasury(currencyTypes.get(x)));
        }

        if (MultiCurrency.getInstance().getMainConfig().disable_essentials) {
            if (Bukkit.getPluginManager().getPlugin("Vault") == null){
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
        MultiCurrency.getInstance().getServer().getServicesManager().unregister(treasuryCurrency);
    }

    public static void reload() {
        unload();
        currencyTypes =  MultiCurrency.getInstance().getCurrencyTypeManager().getAllCurrencyTypes();
        load();
    }

}
