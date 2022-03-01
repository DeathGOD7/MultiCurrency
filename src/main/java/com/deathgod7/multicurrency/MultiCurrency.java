package com.deathgod7.multicurrency;

import com.deathgod7.multicurrency.commands.CommandHandler;
import com.deathgod7.multicurrency.configs.CurrencyConfig;
import com.deathgod7.multicurrency.configs.MainConfig;
import com.deathgod7.multicurrency.data.DatabaseManager;
import com.deathgod7.multicurrency.depends.economy.CurrencyTypeManager;
import com.deathgod7.multicurrency.depends.economy.CurrencyTypes;
import com.deathgod7.multicurrency.depends.economy.TreasuryManager;
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
import java.util.Objects;


public final class MultiCurrency extends JavaPlugin {

    public ConfigHelper configHelper = new ConfigHelper();

    private CurrencyTypeManager currencyTypeManager;
    public CurrencyTypeManager getCurrencyTypeManager(){
        return currencyTypeManager;
    }

    private Messages _messages;
    public Messages getMessages() { return _messages; }

    //getDataFolder().toPath().resolve("somefolder/name.yml")
    public Path getPluginFolder(){
        return getDataFolder().toPath();
    }

    private static MultiCurrency _instance;
    public static MultiCurrency getInstance() {
        return _instance;
    }

    private MainConfig _mainConfig;

    private ConfigManager _mainConfigManager;
    public MainConfig getMainConfig(){
        return  _mainConfig;
    }

    public ConfigManager getMainConfigManager() { return  _mainConfigManager; }

    private Map<String, CurrencyConfig> _currencyConfigs;

    private Map<String, ConfigManager> _currencyConfigsManager;
    public Map<String, CurrencyConfig> getCurrencyConfigs() {
        return _currencyConfigs;
    }

    public Map<String, ConfigManager> getCurrencyConfigsManager() {
        return _currencyConfigsManager;
    }

    private DatabaseManager dbm;
    public DatabaseManager getDbm(){
        return dbm;
    }

    String currencypath;

    TreasuryManager treasuryManager = new TreasuryManager();

    public TreasuryManager getTreasuryManager() {
        return treasuryManager;
    }


    public void ReloadConfigs() {
        // main config
        if (_mainConfig == null){
            _mainConfig = new MainConfig();
        }
        _mainConfigManager = ConfigManager.create(MultiCurrency.getInstance()).target(_mainConfig).saveDefaults().reload();;
        ConsoleLogger.info("Loaded main config from file!", ConsoleLogger.logTypes.log);

        // currency configs
        currencyTypeManager.clearCurrencyTypes();
        configHelper.loadConfigs(currencypath);
        _currencyConfigs = ConfigHelper._configs;
        _currencyConfigsManager = ConfigHelper._configsManager;
    }

    public static PluginDescriptionFile getPDFile() {
        return _instance.getDescription();
    }

    @Override
    public void onEnable() {
        _instance = this;

        currencypath = MultiCurrency.getInstance().getPluginFolder().resolve("Economy").toString();

        // main config
        if (_mainConfig == null){
            _mainConfig = new MainConfig();
        }
        _mainConfigManager = ConfigManager.create(MultiCurrency.getInstance()).target(_mainConfig).saveDefaults().load();

        _messages = Messages.load(MultiCurrency.getInstance());

        currencyTypeManager = new CurrencyTypeManager(MultiCurrency.getInstance());

        if (Bukkit.getPluginManager().getPlugin("Treasury") == null) {
            ConsoleLogger.severe("Required dependent plugin was not found : Treasury", ConsoleLogger.logTypes.log);
            ConsoleLogger.severe("Disabling Multi Currency", ConsoleLogger.logTypes.log);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

//        if (Bukkit.getPluginManager().getPlugin("RedLib") == null) {
//            ConsoleLogger.severe("Required dependent plugin was not found : RedLib", ConsoleLogger.logTypes.log);
//            ConsoleLogger.severe("Disabling Multi Currency", ConsoleLogger.logTypes.log);
//            Bukkit.getPluginManager().disablePlugin(this);
//            return;
//        }


        String configVer = _mainConfig.version;
        String pluginVer = MultiCurrency.getPDFile().getVersion();
        if (!Objects.equals(configVer, pluginVer)){
            _mainConfig.previousversion =  _mainConfig.version;
            _mainConfig.version = MultiCurrency.getPDFile().getVersion();
            getMainConfigManager().save();
        }

        ConsoleLogger.info("Loaded main config from file!", ConsoleLogger.logTypes.log);

        ConsoleLogger.info("Loaded messages from file!", ConsoleLogger.logTypes.log);

        if (dbm == null){
            dbm = new DatabaseManager(MultiCurrency.getInstance());
        }

        ConsoleLogger.info("Loaded database!", ConsoleLogger.logTypes.log);




        Path defaultexample = MultiCurrency.getInstance().getPluginFolder().resolve("Economy");
        if (!Files.exists(defaultexample)) {
            ConfigManager.create(this, defaultexample.resolve("Example Currency.yml")).target(new CurrencyConfig()).saveDefaults().load();
        }

        // currency configs
        configHelper.loadConfigs(currencypath);
        _currencyConfigs = ConfigHelper._configs;
        _currencyConfigsManager = ConfigHelper._configsManager;


        ArgType<CurrencyTypes> currencyType = new ArgType<>("CurrencyType", currencyTypeManager::getCurrencyType);

        new CommandParser(this.getResource("commands.rdcml"), MultiCurrency.getInstance().getMessages())
                .setArgTypes
                (
                   ArgType.of("CurrencyType", currencyTypeManager.getAllCurrencyTypes())
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
        //getMainConfigManager().save();
        //ConsoleLogger.info("Saving config file!!", ConsoleLogger.logTypes.log);
    }
}
