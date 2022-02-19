package com.deathgod7.multicurrency.depends.economy;

import com.deathgod7.multicurrency.utils.ConsoleLogger;
import com.deathgod7.multicurrency.MultiCurrency;

import java.util.NavigableMap;
import java.util.TreeMap;

public class CurrencyTypeManager {

    private final NavigableMap<String, CurrencyTypes> currencyTypes = new TreeMap<>();
    private final MultiCurrency multiCurrency;

    public CurrencyTypeManager(MultiCurrency multiCurrency) {
        this.multiCurrency = multiCurrency;
    }

    public void registerCurrencyType(CurrencyTypes currencyType) {
        if (currencyType == null) {
            ConsoleLogger.warn("Couldn't load currency!! Please check the yaml files.", ConsoleLogger.logTypes.log);
        }
        else {
            ConsoleLogger.info("Loading currency : " + currencyType.getName(), ConsoleLogger.logTypes.log);
            currencyTypes.put(currencyType.getName(), currencyType);
        }
    }

    public CurrencyTypes getCurrencyType(String name) {
        return currencyTypes.get(name);
    }

    public void clear() {
        currencyTypes.clear();
    }

    public NavigableMap<String, CurrencyTypes> getCurrencyTypes() {
        return currencyTypes;
    }

    public int getTotalCurrencyTypes() {
        return currencyTypes.size();
    }

}
