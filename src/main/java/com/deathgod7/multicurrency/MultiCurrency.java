package com.deathgod7.multicurrency;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.config.ConfigManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


public final class MultiCurrency extends JavaPlugin {

    public ConfigHelper configHelper = new ConfigHelper();

    //getDataFolder().toPath().resolve("somefolder/name.yml")
    public Path getPluginFolder(){
        return getDataFolder().toPath();
    }

    private static MultiCurrency _instance;
    public static MultiCurrency getInstance() {
        return _instance;
    }

    private ConfigManager _mainConfig;
    public ConfigManager getMainConfig(){
        return  _mainConfig;
    }

    private Map<String, ConfigManager> _currencyConfigs;
    public Map<String, ConfigManager> getCurrencyConfigs() {
        if (_currencyConfigs == null) {
            MultiCurrency.getInstance()._currencyConfigs = configHelper.getConfigs();
        }
        return _currencyConfigs;
    }


    public void ReloadConfigs() {
        // main config
        MultiCurrency.getInstance()._mainConfig = ConfigManager.create(MultiCurrency.getInstance()).target(ConfigFile.MainConfig.class).saveDefaults().reload();;
    }

    public static PluginDescriptionFile getPDFile() {
        return _instance.getDescription();
    }

    @Override
    public void onEnable() {

        _instance = this;
        _mainConfig = ConfigManager.create(this).target(ConfigFile.MainConfig.class).saveDefaults().load();

        Path defaultexample = MultiCurrency.getInstance().getPluginFolder().resolve("Economy");
        if (!Files.exists(defaultexample)) {
            ConfigManager.create(this, defaultexample.resolve("Example Currency.yml")).target(ConfigFile.CurrencyConfig.class).saveDefaults().load();
        }


        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            ConsoleLogger.severe("Required dependent plugin was not found : Vault", ConsoleLogger.logTypes.log);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("RedLib") == null) {
            ConsoleLogger.severe("Required dependent plugin was not found : RedLib", ConsoleLogger.logTypes.log);
            return;
        }

        ConsoleLogger.info("All files loaded", ConsoleLogger.logTypes.log);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
