package com.deathgod7.multicurrency.commands;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

import java.util.Objects;

import static com.deathgod7.multicurrency.utils.TextUtils.ConvertTextColor;

public class CommandHandler {

    private final MultiCurrency multiCurrency;

    public CommandHandler(MultiCurrency multiCurrency) {
        this.multiCurrency = multiCurrency;
    }

//    @CommandHook("info")
//    public void info(CommandSender commandSender){
//
//    }

    @CommandHook("info")
    public void info(CommandSender commandSender){
        String databaseType;
        String isConnected;
        String version = ConvertTextColor('&', "&ev"+MultiCurrency.getPDFile().getVersion());
        String apiversion = ConvertTextColor('&', "&e"+MultiCurrency.getPDFile().getAPIVersion());
        String developer = ConvertTextColor('&', "&1"+MultiCurrency.getPDFile().getAuthors());

        String temp1 = multiCurrency.getConfig().getString("Database.type");
        if (Objects.equals(temp1, "sqlite")){
            databaseType = ConvertTextColor('&', "&3SQLite");
        }
        else if (Objects.equals(temp1, "mysql")){
            databaseType = ConvertTextColor('&', "&3MySQL");
        }
        else{
            databaseType = ConvertTextColor('&', "&3SQLite");
        }

        if (MultiCurrency.getInstance().getDbm().getSQLite().isConnected()){
            isConnected = ConvertTextColor('&', "&2ONLINE");
        }
        else{
            isConnected = ConvertTextColor('&', "&4OFFLINE");
        }

        commandSender.sendMessage("Multi Currency");
        commandSender.sendMessage("Version : " + version);
        commandSender.sendMessage("Developer(s) : " + developer);
        commandSender.sendMessage("API Version : " + apiversion);
        commandSender.sendMessage("Database : " + databaseType);
        commandSender.sendMessage("Status : " + isConnected);
        MultiCurrency.getInstance().getDbm().loadSqliteTable();
    }

    @CommandHook("reload")
    public void reload(CommandSender commandSender){
        commandSender.sendMessage("Reloading all configs!");
        MultiCurrency.getInstance().ReloadConfigs();
        commandSender.sendMessage("All configs are reloaded!");
    }

    @CommandHook("listcurrency")
    public void listcurrency(CommandSender commandSender) {
        commandSender.sendMessage("listcurrency will be implemented soon!");
        StringBuilder temp = new StringBuilder();
        for (String x : MultiCurrency.getInstance().getCurrencyTypeManager().listAvailableCurrency()) {
            temp.append(x).append(", ");
        }
        String ftemp = temp.substring(0, temp.length() - 2);
        commandSender.sendMessage("Available Currency : " + ftemp);
    }

    @CommandHook("add")
    public void add(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        commandSender.sendMessage("add will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Target : " + target.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
        commandSender.sendMessage("Currency Amount : " + amount);
        commandSender.sendMessage("Is Silent : " + isSilent);
    }

    @CommandHook("set")
    public void set(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        commandSender.sendMessage("set will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Target : " + target.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
        commandSender.sendMessage("Currency Amount : " + amount);
        commandSender.sendMessage("Is Silent : " + isSilent);
    }

    @CommandHook("take")
    public void take(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        commandSender.sendMessage("take will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Target : " + target.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
        commandSender.sendMessage("Currency Amount : " + amount);
        commandSender.sendMessage("Is Silent : " + isSilent);
    }

    @CommandHook("give")
    public void give(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        commandSender.sendMessage("give will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Target : " + target.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
        commandSender.sendMessage("Currency Amount : " + amount);
        commandSender.sendMessage("Is Silent : " + isSilent);
    }

    @CommandHook("reset")
    public void reset(CommandSender commandSender, Player target, CurrencyType currencyType, boolean isSilent) {
        commandSender.sendMessage("reset will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Target : " + target.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
        commandSender.sendMessage("Is Silent : " + isSilent);
    }

    @CommandHook("bal")
    public void balself(CommandSender commandSender, CurrencyType currencyType) {
        commandSender.sendMessage("bal will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
    }

    @CommandHook("balother")
    public void balother(CommandSender commandSender, CurrencyType currencyType, Player target) {
        commandSender.sendMessage("balother will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Target : " + target.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
    }

    @CommandHook("baltop")
    public void baltop(CommandSender commandSender, CurrencyType currencyType) {
        commandSender.sendMessage("balother will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
    }

}
