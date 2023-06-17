package com.deathgod7.multicurrency.utils;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.configs.CurrencyConfig;
import com.deathgod7.multicurrency.data.DatabaseManager;
import com.deathgod7.multicurrency.data.helper.Column;
import com.deathgod7.multicurrency.data.helper.CurrencyTable;
import com.deathgod7.multicurrency.data.helper.Table;
import com.deathgod7.multicurrency.data.sqlite.SQLite;
import com.deathgod7.multicurrency.depends.economy.CurrencyTypeManager;
import redempt.redlib.config.ConfigManager;

import javax.xml.crypto.Data;
import java.io.File;
import java.math.BigDecimal;
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

		if (!dbm.getTables().containsKey("TreasuryAccounts")) {
			dbm.createAccountTable();
		}
		else {
			ConsoleLogger.warn("TreasuryAccounts table exists in database!", ConsoleLogger.logTypes.debug);
		}

		if (!dbm.getTables().containsKey("Transactions")) {
			dbm.createTransactionTable();
		}
		else {
			ConsoleLogger.warn("Transactions table exists in database!", ConsoleLogger.logTypes.debug);
		}

		CurrencyTypeManager currencyTypeManager = instance.getCurrencyTypeManager();

		for (String x:configloc) {
			CurrencyConfig ccfg = new CurrencyConfig();
			ConfigManager cfg = ConfigManager.create(instance, "Economy" + "/" + x).target(ccfg).saveDefaults().load();

			String currencyName = ccfg.currency.getName();
			currencyConfigs.put(currencyName, ccfg);
			currencyConfigsManager.put(currencyName, cfg);

			currencyTypeManager.registerCurrencyType(ccfg.currency);

			ConsoleLogger.info(String.format("Loaded %s currency config from file!", currencyName), ConsoleLogger.logTypes.log);

//            ConsoleLogger.info(String.format("Test confighelper X : %s", ccfg.currency.getCurrencySymbol()), ConsoleLogger.logTypes.log);

			if (!dbm.getTables().containsKey(currencyName)) {
				Table table = new Table(currencyName, CurrencyTable.CurrencyData());
				dbm.createTable(table);
			}
			else {
				ConsoleLogger.warn(String.format("%s currency table exists in database!", currencyName), ConsoleLogger.logTypes.debug);
			}

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
