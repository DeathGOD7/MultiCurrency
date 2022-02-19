package com.deathgod7.multicurrency.utils;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.configs.ConfigFile;
import redempt.redlib.config.ConfigManager;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public final class ConfigHelper {
    private Map<String, ConfigManager> _configs;

    public Map<String, ConfigManager> getConfigs() {
        if (_configs == null) {
             _configs = new HashMap<>();
            List<String> configloc = listConfigs(MultiCurrency.getInstance().getPluginFolder().resolve("Economy").toString());

            for (String x:configloc) {
                ConfigManager cfg = ConfigManager.create(MultiCurrency.getInstance(), x).target(ConfigFile.CurrencyConfig.class).saveDefaults().load();
                _configs.put(cfg.getConfig().getString("name"), cfg);
            }

        }

        return _configs;
    }

    public void ReloadConfigs() {
         // eco config (auto reloads when doing get)
        List<String> configloc = listConfigs(MultiCurrency.getInstance().getPluginFolder().resolve("Economy").toString());
        _configs.clear();
        for (String x:configloc) {
            ConfigManager cfg = ConfigManager.create(MultiCurrency.getInstance(), x).target(ConfigFile.CurrencyConfig.class).saveDefaults().reload();
            _configs.put(cfg.getConfig().getString("name"), cfg);
        }
    }

    public static List<String> listConfigs(String path) {
        List<String> configs = new ArrayList<>();
        for (String file : Arrays.stream(Objects.requireNonNull((new File(path)).listFiles())).map(File::getName).collect(Collectors.toList())) {
            if (file.contains(".yml")) configs.add(file.substring(0, file.lastIndexOf('.')));
        }
        return configs;
    }
}
