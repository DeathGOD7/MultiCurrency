package com.deathgod7.multicurrency.commands;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.DataFormatter;
import com.deathgod7.multicurrency.data.DatabaseManager;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.deathgod7.multicurrency.depends.economy.treasury.TreasuryManager;
import com.deathgod7.multicurrency.utils.TextUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

import java.math.BigDecimal;
import java.util.Objects;

import static com.deathgod7.multicurrency.utils.TextUtils.ConvertTextColor;

public class CommandHandler {

    private final MultiCurrency instance;
    private final DatabaseManager dbm;
    private final TreasuryManager treasuryManager;

    public CommandHandler(MultiCurrency instance) {
        this.instance = instance;
        this.dbm = instance.getDBM();
        this.treasuryManager = instance.getTreasuryManager();
    }

    @CommandHook("info")
    public void info(CommandSender commandSender){
        String databaseType;
        String isConnected;
        String version = ConvertTextColor('&', "&ev"+MultiCurrency.getPDFile().getVersion());
        String apiversion = ConvertTextColor('&', "&e"+MultiCurrency.getPDFile().getAPIVersion());
        String developer = ConvertTextColor('&', "&1"+MultiCurrency.getPDFile().getAuthors());

        String temp1 = instance.getConfig().getString("Database.type");
        if (Objects.equals(temp1, "sqlite")){
            databaseType = ConvertTextColor('&', "&3SQLite");
        }
        else if (Objects.equals(temp1, "mysql")){
            databaseType = ConvertTextColor('&', "&3MySQL");
        }
        else{
            databaseType = ConvertTextColor('&', "&3SQLite");
        }

        if (dbm.isConnected()){
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
        //if


    }

    @CommandHook("reload")
    public void reload(CommandSender commandSender){
        String warning = "&aIt is best to restart the server as reloading will just break the plugin.";
        String warning2 = "&aIf you want this feature then please request in discord with good reason.";
        commandSender.sendMessage(ConvertTextColor('&', warning));
        commandSender.sendMessage(ConvertTextColor('&', warning2));
    }

    @CommandHook("debug")
    public void debug(CommandSender commandSender) {
        if (instance.getMainConfig().debug) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
        }

        StringBuilder temp = new StringBuilder();
        for (String x : instance.getTreasuryManager().getTreasuryCurrency().keySet()) {
            temp.append(x).append(", ");
        }
        String ftemp = temp.substring(0, temp.length() - 2);
        commandSender.sendMessage("Available Currency : " + ftemp);

        StringBuilder temp2 = new StringBuilder();
        for (String x : instance.getDBM().getTables().keySet()) {
            temp2.append(x).append(", ");
        }
        String ftemp2 = temp2.substring(0, temp2.length() - 2);
        commandSender.sendMessage("Available Table : " + ftemp2);
    }

    @CommandHook("add")
    public void add(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        if (instance.getMainConfig().debug) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
            commandSender.sendMessage("Currency Amount : " + amount);
            commandSender.sendMessage("Is Silent : " + isSilent);
        }



    }

    @CommandHook("set")
    public void set(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        if (instance.getMainConfig().debug) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
            commandSender.sendMessage("Currency Amount : " + amount);
            commandSender.sendMessage("Is Silent : " + isSilent);
        }



    }

    @CommandHook("take")
    public void take(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        if (instance.getMainConfig().debug) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
            commandSender.sendMessage("Currency Amount : " + amount);
            commandSender.sendMessage("Is Silent : " + isSilent);
        }



    }

    @CommandHook("give")
    public void give(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        if (instance.getMainConfig().debug) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
            commandSender.sendMessage("Currency Amount : " + amount);
            commandSender.sendMessage("Is Silent : " + isSilent);
        }



    }

    @CommandHook("reset")
    public void reset(CommandSender commandSender, Player target, CurrencyType currencyType, boolean isSilent) {
        if (instance.getMainConfig().debug) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
            commandSender.sendMessage("Is Silent : " + isSilent);
        }



    }

    @CommandHook("bal")
    public void balself(CommandSender commandSender, CurrencyType currencyType) {
        if (instance.getMainConfig().debug) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
        }

        if (commandSender.getName().equalsIgnoreCase("CONSOLE")) {
            commandSender.sendMessage(TextUtils.ConvertTextColor("&4Cannot invoke this command from server console!!"));
            commandSender.sendMessage(TextUtils.ConvertTextColor("&4Please specify player if you want to view players balance."));
            return;
        }

        Player player = (Player) commandSender;

        //if ()


        if (!dbm.doesUserExists(player, currencyType)) {
            dbm.createUser(player, currencyType);
        }

        DataFormatter dataFormatter = currencyType.getDataFormatter();
        BigDecimal amount = new BigDecimal(dbm.getBalance(player,currencyType).toString());

        commandSender.sendMessage("Your balance is " + dataFormatter.formatBigDecimal(amount));

    }

    @CommandHook("balother")
    public void balother(CommandSender commandSender, CurrencyType currencyType, Player target) {
        if (instance.getMainConfig().debug) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
        }

        if (!dbm.doesUserExists(target, currencyType)) {
            dbm.createUser(target, currencyType);
        }

        DataFormatter dataFormatter = currencyType.getDataFormatter();
        BigDecimal amount = new BigDecimal(dbm.getBalance(target,currencyType).toString());

        commandSender.sendMessage("Your balance is " + dataFormatter.formatBigDecimal(amount));
    }

    @CommandHook("baltop")
    public void baltop(CommandSender commandSender, CurrencyType currencyType) {
        commandSender.sendMessage("balother will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
    }

}
