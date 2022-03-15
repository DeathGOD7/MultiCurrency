package com.deathgod7.multicurrency.utils;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.configs.CurrencyConfig;
import com.deathgod7.multicurrency.data.DatabaseManager;
import com.deathgod7.multicurrency.data.helper.Column;
import com.deathgod7.multicurrency.data.helper.Table;
import com.deathgod7.multicurrency.data.sqlite.SQLite;
import redempt.redlib.config.ConfigManager;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public final class ConfigHelper {
    MultiCurrency instance;
    DatabaseManager dbm;
    private HashMap<String, CurrencyConfig> currencyConfigs;
    public HashMap<String, CurrencyConfig> getCurrencyConfigs() {
        return currencyConfigs;
    }

    private HashMap<String, ConfigManager> currencyConfigsManager;
    public HashMap<String, ConfigManager> getCurrencyConfigsManager() {
        return currencyConfigsManager;
    }

    public ConfigHelper(MultiCurrency instance) {
        this.instance = instance;
        this.dbm = instance.getDBM();
    }

    public void loadConfigs(String path) {
        currencyConfigs = new HashMap<>();
        currencyConfigsManager = new HashMap<>();
        List<String> configloc = listConfigs(path);

        dbm.createAccountTable();

        for (String x:configloc) {
            CurrencyConfig ccfg = new CurrencyConfig();
            ConfigManager cfg = ConfigManager.create(MultiCurrency.getInstance(), "Economy" + "/" + x).target(ccfg).saveDefaults().load();

            String currencyName = ccfg.currency.getName();
            currencyConfigs.put(currencyName, ccfg);
            currencyConfigsManager.put(currencyName, cfg);

            MultiCurrency.getInstance().getCurrencyTypeManager().registerCurrencyType(ccfg.currency);

            ConsoleLogger.info(String.format("Loaded %s currency config from file!", currencyName), ConsoleLogger.logTypes.log);

            List<Column> temp = new ArrayList<>();
            Column uuid = new Column("UUID", DatabaseManager.DataType.STRING, 100);
            Column playername = new Column("Name", DatabaseManager.DataType.STRING, 100);
            Column money = new Column("Money", DatabaseManager.DataType.STRING, 100);

            temp.add(uuid);
            temp.add(playername);
            temp.add(money);

           Table table = new Table(currencyName, temp);

           dbm.createTable(table);

        }


    }


    public static List<String> listConfigs(String path) {
        List<String> configs = new ArrayList<>();
        for (String file : Arrays.stream(Objects.requireNonNull((new File(path)).listFiles())).map(File::getName).collect(Collectors.toList())) {
            if (file.contains(".yml")) configs.add(file);
        }
        return configs;
    }
}
