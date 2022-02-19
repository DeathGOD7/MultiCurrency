package com.deathgod7.multicurrency;

import com.deathgod7.multicurrency.commands.CommandHandler;
import com.deathgod7.multicurrency.configs.ConfigFile;
import com.deathgod7.multicurrency.depends.economy.CurrencyTypeManager;
import com.deathgod7.multicurrency.depends.economy.CurrencyTypes;
import com.deathgod7.multicurrency.utils.ConfigHelper;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.config.ConfigManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


public final class MultiCurrency extends JavaPlugin {

    public ConfigHelper configHelper = new ConfigHelper();

    CurrencyTypeManager currencyTypeManager;

    Messages _messages;
    public Messages getMessages() { return _messages; }

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

        currencyTypeManager = new CurrencyTypeManager(MultiCurrency.getInstance());

        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            ConsoleLogger.severe("Required dependent plugin was not found : Vault", ConsoleLogger.logTypes.log);
            ConsoleLogger.severe("Disabling Multi Currency", ConsoleLogger.logTypes.log);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("RedLib") == null) {
            ConsoleLogger.severe("Required dependent plugin was not found : RedLib", ConsoleLogger.logTypes.log);
            ConsoleLogger.severe("Disabling Multi Currency", ConsoleLogger.logTypes.log);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        _mainConfig = ConfigManager.create(this).target(ConfigFile.MainConfig.class).saveDefaults().load();
        ConsoleLogger.info("Loaded main config from file!", ConsoleLogger.logTypes.log);


        _messages = Messages.load(MultiCurrency.getInstance());
        ConsoleLogger.info("Loaded messages from file!", ConsoleLogger.logTypes.log);


        Path defaultexample = MultiCurrency.getInstance().getPluginFolder().resolve("Economy");
        if (!Files.exists(defaultexample)) {
            ConfigManager.create(this, defaultexample.resolve("Example Currency.yml")).target(ConfigFile.CurrencyConfig.class).saveDefaults().load();
        }

        ArgType<CurrencyTypes> currencyType = new ArgType<>("CurrencyType", currencyTypeManager::getCurrencyType);

        new CommandParser(this.getResource("commands.rdcml"), this.getMessages())
                .setArgTypes
                (
                   ArgType.of("CurrencyType", currencyTypeManager.getCurrencyTypes())
                )
                .parse()
                .register("multicurrency",
                        new CommandHandler(MultiCurrency.getInstance()
                        )
                );

        ConsoleLogger.info("All files loaded", ConsoleLogger.logTypes.log);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
