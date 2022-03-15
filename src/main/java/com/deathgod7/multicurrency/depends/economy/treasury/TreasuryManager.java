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

    private HashMap<String, Currency> treasuryCurrency;
    public HashMap<String, Currency> getTreasuryCurrency() {
        return treasuryCurrency;
    }

    private HashMap<String, CurrencyType> currencyTypes;
    public HashMap<String, CurrencyType> getCurrencyTypes() {
        return currencyTypes;
    }

    public TreasuryManager (MultiCurrency instance){
        this.instance = instance;
        treasuryAccountmanager = new TreasuryAccountManager(instance);
        treasuryHook = new TreasuryHook(instance);
        this.treasuryCurrency = new HashMap<>();
        this.currencyTypes = MultiCurrency.getInstance().getCurrencyTypeManager().getAllCurrencyTypes();
        load();
    }


    private Currency convertToTreasury(CurrencyType currencyType){
        return new TreasuryCurrency(currencyType);
    }

    public void load() {

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

    public void unload() {
        MultiCurrency.getInstance().getServer().getServicesManager().unregister(treasuryCurrency);
    }

    public void reload() {
        unload();
        currencyTypes =  MultiCurrency.getInstance().getCurrencyTypeManager().getAllCurrencyTypes();
        load();
    }

}
