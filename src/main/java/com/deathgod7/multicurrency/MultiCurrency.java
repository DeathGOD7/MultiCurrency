package com.deathgod7.multicurrency;

import com.deathgod7.multicurrency.commands.CommandHandler;
import com.deathgod7.multicurrency.configs.CurrencyConfig;
import com.deathgod7.multicurrency.configs.MainConfig;
import com.deathgod7.multicurrency.data.DatabaseManager;
import com.deathgod7.multicurrency.depends.economy.CurrencyTypeManager;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.deathgod7.multicurrency.depends.economy.treasury.TreasuryAccountManager;
import com.deathgod7.multicurrency.depends.economy.treasury.TreasuryManager;
import com.deathgod7.multicurrency.events.EventHandlers;
import com.deathgod7.multicurrency.utils.ConfigHelper;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import me.lokka30.treasury.api.common.service.ServicePriority;
import me.lokka30.treasury.api.common.service.ServiceRegistry;
import me.lokka30.treasury.api.economy.EconomyProvider;
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

    public ConfigHelper configHelper;

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
    public MainConfig getMainConfig(){
        return  _mainConfig;
    }

    private ConfigManager _mainConfigManager;
    public ConfigManager getMainConfigManager() { return  _mainConfigManager; }

    private Map<String, CurrencyConfig> _currencyConfigs;
    public Map<String, CurrencyConfig> getCurrencyConfigs() {
        return _currencyConfigs;
    }

    private Map<String, ConfigManager> _currencyConfigsManager;
    public Map<String, ConfigManager> getCurrencyConfigsManager() {
        return _currencyConfigsManager;
    }

    private DatabaseManager dbm;
    public DatabaseManager getDBM(){
        return dbm;
    }

    String currencypath;

    TreasuryManager treasuryManager;
    public TreasuryManager getTreasuryManager() {
        return treasuryManager;
    }

    TreasuryAccountManager treasuryAccountmanager;
    public TreasuryAccountManager getTreasuryAccountmanager() {
        return treasuryAccountmanager;
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
        _currencyConfigs = configHelper.getCurrencyConfigs();
        _currencyConfigsManager = configHelper.getCurrencyConfigsManager();
    }


    public static PluginDescriptionFile getPDFile() {
        return _instance.getDescription();
    }

    @Override
    public void onEnable() {
        _instance = this;
		
		if (Bukkit.getPluginManager().getPlugin("RedLib") == null) {
            getLogger().info("Required dependent plugin was not found : RedLib");
            getLogger().info("Disabling " + this.getName());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
				
		if (Bukkit.getPluginManager().getPlugin("Treasury") == null) {
            getLogger().info("Required dependent plugin was not found : Treasury");
            getLogger().info("Disabling " + this.getName());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        currencypath = MultiCurrency.getInstance().getPluginFolder().resolve("Economy").toString();

        // main config
        if (_mainConfig == null){
            _mainConfig = new MainConfig();
        }
        _mainConfigManager = ConfigManager.create(MultiCurrency.getInstance()).target(_mainConfig).saveDefaults().load();

        _messages = Messages.load(MultiCurrency.getInstance());

        currencyTypeManager = new CurrencyTypeManager(MultiCurrency.getInstance());

        String configVer = _mainConfig.version;
        String pluginVer = MultiCurrency.getPDFile().getVersion();
        if (!Objects.equals(configVer, pluginVer)){
            _mainConfig.previousversion =  _mainConfig.version;
            _mainConfig.version = MultiCurrency.getPDFile().getVersion();
            getMainConfigManager().save();
        }

        ConsoleLogger.info("Loaded main config from file!", ConsoleLogger.logTypes.log);

        ConsoleLogger.info("Loaded messages from file!", ConsoleLogger.logTypes.log);

        // Load database
        if (dbm == null){
            dbm = new DatabaseManager(MultiCurrency.getInstance());
        }

        Path defaultexample = MultiCurrency.getInstance().getPluginFolder().resolve("Economy");
        if (!Files.exists(defaultexample)) {
            ConfigManager.create(this, defaultexample.resolve("Example Currency.yml")).target(new CurrencyConfig()).saveDefaults().load();
        }

        // currency configs
        configHelper = new ConfigHelper(MultiCurrency.getInstance());
        configHelper.loadConfigs(currencypath);
        _currencyConfigs = configHelper.getCurrencyConfigs();
        _currencyConfigsManager = configHelper.getCurrencyConfigsManager();

        // manages all treasuy things
        treasuryManager  = new TreasuryManager(MultiCurrency.getInstance());

        treasuryAccountmanager = new TreasuryAccountManager(MultiCurrency.getInstance());

        ArgType<CurrencyType> currencyType = new ArgType<>("CurrencyType", currencyTypeManager::getCurrencyType);

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

        // Register to Treasury
        ServiceRegistry.INSTANCE.registerService(
                EconomyProvider.class,
                treasuryManager.getTreasuryHook(),
                getName(),
                ServicePriority.NORMAL
        );

        // Register Events
        EventHandlers eventHandler = new EventHandlers(MultiCurrency.getInstance());
        this.getServer().getPluginManager().registerEvents(eventHandler, MultiCurrency.getInstance());


        ConsoleLogger.info("Hooked to Treasury", ConsoleLogger.logTypes.log);


        ConsoleLogger.info("All files loaded", ConsoleLogger.logTypes.log);

    }

    @Override
    public void onDisable() {
        //some usage idk XD

        // Unregister to Treasury
        if (Bukkit.getPluginManager().getPlugin("Treasury") != null) {
            ServiceRegistry.INSTANCE.unregister(
                    EconomyProvider.class,
                    treasuryManager.getTreasuryHook()
            );
        }

    }
}
