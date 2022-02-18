package com.deathgod7.multicurrency;

import com.deathgod7.multicurrency.depends.economy.CurrencyTypes;
import redempt.redlib.config.annotations.*;

import java.util.HashMap;
import java.util.Map;

public final class ConfigFile {
    static String version = MultiCurrency.getPDFile().getVersion();
    //@ConfigMappable
    //public record Settings(String version, String previousversion, boolean debug, boolean disable_essentials) {}

//    @ConfigMappable
    public static class MainConfig {
        @Comment("---------------------------------------------------------------------------")
        @Comment("                                                                           ")
        @Comment("                     Multi Currency - by Death GOD 7                       ")
        @Comment("                                - A solution to your custom needs          ")
        @Comment("                                                                           ")
        @Comment("                 [ Github : https://github.com/DeathGOD7 ]                 ")
        @Comment("                  [ Wiki : https://github.com/DeathGOD7 ]                  ")
        @Comment("                                                                           ")
        @Comment("---------------------------------------------------------------------------")
        @Comment("")
        @Comment("Some settings of the plugin which might come handy for your server and for debugging plugin.")
        @Comment("version = The plugin version you are using")
        @Comment("previousversion = The plugin version that you updated from")
        @Comment("debug = This allows you to get additional plugin information. Really really helpful for debugging")
        @Comment("disable_essentials = This will disable essentials economy vault hook")

        @ConfigName("Settings.version")
        public static String version = ConfigFile.version;
        @ConfigName("Settings.previousversion")
        public static String previousversion = "";
        //@ConfigName("Settings.firstrun")
        //public static boolean firstrun = true;
        @ConfigName("Settings.debug")
        public static boolean debug = false;
        @ConfigName("Settings.disable_essentials")
        public static boolean disable_essentials = true;

        @ConfigPostInit
        public void postInit() {
            ConsoleLogger.info("Loaded main config from file!", ConsoleLogger.logTypes.log);
        }



    }

//    @ConfigMappable
    public static class CurrencyConfig{
        @Comment("---------------------------------------------------------------------------")
        @Comment("                                                                           ")
        @Comment("                     Multi Currency - by Death GOD 7                       ")
        @Comment("                                - A solution to your custom needs          ")
        @Comment("                                                                           ")
        @Comment("                 [ Github : https://github.com/DeathGOD7 ]                 ")
        @Comment("                  [ Wiki : https://github.com/DeathGOD7 ]                  ")
        @Comment("                                                                           ")
        @Comment("---------------------------------------------------------------------------")
        @Comment("")
        @Comment("Currency File Configs")
        @Comment("")
        public static CurrencyTypes currency = new CurrencyTypes();



        @ConfigPostInit
        public void postInit() {
            ConsoleLogger.info(String.format("Loaded %s currency config from file!", currency.getName()), ConsoleLogger.logTypes.log);
        }

    }




}
