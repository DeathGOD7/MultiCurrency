package com.deathgod7.multicurrency.depends.economy;

import com.deathgod7.multicurrency.utils.ConsoleLogger;
import com.deathgod7.multicurrency.MultiCurrency;

import java.util.*;

public class CurrencyTypeManager {

    private final HashMap<String, CurrencyTypes> currencyTypes = new HashMap<>();
    private final MultiCurrency multiCurrency;
    private final List<String> availableCurrency;

    public CurrencyTypeManager(MultiCurrency multiCurrency) {
        this.multiCurrency = multiCurrency;
        availableCurrency = new ArrayList<>();
    }

    public void registerCurrencyType(CurrencyTypes currencyType) {
        if (currencyType == null) {
            ConsoleLogger.warn("Couldn't load currency!! Please check the yaml files.", ConsoleLogger.logTypes.log);
        }
        else {
            ConsoleLogger.info("Loading currency : " + currencyType.getName(), ConsoleLogger.logTypes.log);

            currencyTypes.put(currencyType.getName(), currencyType);
            availableCurrency.add(currencyType.getName());
        }
    }


    public void clearCurrencyTypes(){
        currencyTypes.clear();
        availableCurrency.clear();
    }

    public CurrencyTypes getCurrencyType(String name) {
        return currencyTypes.get(name);
    }

    public HashMap<String, CurrencyTypes> getAllCurrencyTypes() {
        return currencyTypes;
    }

    public List<String> listAvailableCurrency(){
        return availableCurrency;
    }

    public int getTotalCurrencyTypes() {
        return currencyTypes.size();
    }

}
