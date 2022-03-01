package com.deathgod7.multicurrency.depends.economy;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.configs.CurrencyConfig;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import redempt.redlib.config.ConfigManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class VaultHook {
    public static List<Economy> econ = null;
    public static Permission vaultPerm = null;
    public static Map<String, CurrencyConfig> ecoconfigs = MultiCurrency.getInstance().getCurrencyConfigs();

    @SuppressWarnings("ConstantConditions")
    public static void load() {

        for (String x : ecoconfigs.keySet()) {
            econ.add(new Vault(ecoconfigs.get(x)));
        }

        RegisteredServiceProvider<Permission> rsp = MultiCurrency.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp != null) {
            vaultPerm = rsp.getProvider();
        }
        for (Economy eco : econ) {
            MultiCurrency.getInstance().getServer().getServicesManager().register(Economy.class, eco, MultiCurrency.getInstance(), ServicePriority.Normal);
        }

        if (MultiCurrency.getInstance().getMainConfig().disable_essentials) {
            Collection<RegisteredServiceProvider<Economy>> econs = Bukkit.getPluginManager().getPlugin("Vault").getServer().getServicesManager().getRegistrations(Economy.class);
            for (RegisteredServiceProvider<Economy> allecon : econs) {
                if (allecon.getProvider().getName().equalsIgnoreCase("Essentials Economy")) {
                    MultiCurrency.getInstance().getServer().getServicesManager().unregister(allecon.getProvider());
                }
            }
        }
    }

    public static void unload() {
        MultiCurrency.getInstance().getServer().getServicesManager().unregister(econ);
    }

    public static void reload() {
        unload();
        ecoconfigs =  MultiCurrency.getInstance().getCurrencyConfigs();
        load();
    }
}
