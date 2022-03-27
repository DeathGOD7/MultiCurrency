package com.deathgod7.multicurrency.commands;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.DataFormatter;
import com.deathgod7.multicurrency.data.DatabaseManager;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.deathgod7.multicurrency.depends.economy.treasury.TreasuryAccountManager;
import com.deathgod7.multicurrency.depends.economy.treasury.TreasuryManager;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import com.deathgod7.multicurrency.utils.SE7ENUtils;
import com.deathgod7.multicurrency.utils.TextUtils;
import me.lokka30.treasury.api.economy.account.NonPlayerAccount;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.CommandHook;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static com.deathgod7.multicurrency.utils.TextUtils.ConvertTextColor;

public class CommandHandler {

    private final MultiCurrency instance;
    private final DatabaseManager dbm;
    private final TreasuryManager treasuryManager;
    private final TreasuryAccountManager tAM;

    public CommandHandler(MultiCurrency instance) {
        this.instance = instance;
        this.dbm = instance.getDBM();
        this.treasuryManager = instance.getTreasuryManager();
        this.tAM = instance.getTreasuryAccountmanager();
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
        if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
            commandSender.sendMessage("Currency Amount : " + amount);
            commandSender.sendMessage("Is Silent : " + isSilent);
        }

    }

    @CommandHook("set")
    public void set(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
            commandSender.sendMessage("Currency Amount : " + amount);
            commandSender.sendMessage("Is Silent : " + isSilent);
        }



    }

    @CommandHook("take")
    public void take(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
            commandSender.sendMessage("Currency Amount : " + amount);
            commandSender.sendMessage("Is Silent : " + isSilent);
        }



    }

    @CommandHook("give")
    public void give(CommandSender commandSender, Player target, CurrencyType currencyType, int amount, boolean isSilent) {
        if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {

            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
            commandSender.sendMessage("Currency Amount : " + amount);
            commandSender.sendMessage("Is Silent : " + isSilent);
        }



    }

    @CommandHook("reset")
    public void reset(CommandSender commandSender, Player target, CurrencyType currencyType, boolean isSilent) {
        if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
            commandSender.sendMessage("Is Silent : " + isSilent);
        }



    }

    @CommandHook("bal")
    public void balself(CommandSender commandSender, CurrencyType currencyType) {
        if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
        }

        if (!commandSender.getName().equalsIgnoreCase("PLAYER")) {
            commandSender.sendMessage(TextUtils.ConvertTextColor("&4Cannot invoke this command from server console!!"));
            commandSender.sendMessage(TextUtils.ConvertTextColor("&4Please specify player if you want to view players balance."));
            return;
        }

        Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

        if (currency == null) {
            commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
            commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
            return;
        }

        Player player = (Player) commandSender;
        DataFormatter dataFormatter = new DataFormatter(currencyType);
        final BigDecimal[] amount = {BigDecimal.ZERO};

        if ( !tAM.hasPlayerAccount(player.getUniqueId() )) {
            tAM.registerPlayerAccount(player.getUniqueId());
        }

        PlayerAccount playerAccount = tAM.getPlayerAccount(player.getUniqueId());

        playerAccount.retrieveBalance(currency,
            new EconomySubscriber<BigDecimal>() {
                @Override
                public void succeed(@NotNull BigDecimal bigDecimal) {
                    amount[0] = bigDecimal;
                }

                @Override
                public void fail(@NotNull EconomyException exception) {
                    commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't get player account from database!!"));
                    ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
                }
            }
        );

        commandSender.sendMessage("Your balance is " + dataFormatter.formatBigDecimal(amount[0], false));

    }

    @CommandHook("balother")
    public void balother(CommandSender commandSender, CurrencyType currencyType, Player target) {
        if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target.getName());
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
        }

        Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

        if (currency == null) {
            commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
            commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
            return;
        }

        DataFormatter dataFormatter = new DataFormatter(currencyType);
        final BigDecimal[] amount = {BigDecimal.ZERO};

        if ( !tAM.hasPlayerAccount(target.getUniqueId() )) {
            tAM.registerPlayerAccount(target.getUniqueId());
        }

        PlayerAccount playerAccount = tAM.getPlayerAccount(target.getUniqueId());

        playerAccount.retrieveBalance(currency,
                new EconomySubscriber<BigDecimal>() {
                    @Override
                    public void succeed(@NotNull BigDecimal bigDecimal) {
                        amount[0] = bigDecimal;
                    }

                    @Override
                    public void fail(@NotNull EconomyException exception) {
                        commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't get player account from database!!"));
                        ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
                    }
                }
        );

        commandSender.sendMessage(target.getName() + " balance is " + dataFormatter.formatBigDecimal(amount[0], false));
    }

    @CommandHook("balother2")
    public void balother2(CommandSender commandSender, CurrencyType currencyType, String target) {
        if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
            commandSender.sendMessage("Command Sender : " + commandSender.getName());
            commandSender.sendMessage("Target : " + target);
            commandSender.sendMessage("Currency Type : " + currencyType.getName());
        }

        Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

        if (currency == null) {
            commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
            commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
            return;
        }

        DataFormatter dataFormatter = new DataFormatter(currencyType);
        UUID playerID = Bukkit.getPlayerUniqueId(target);
        final BigDecimal[] amount = {BigDecimal.ZERO};

        if (playerID == null) {
            if ( !tAM.hasNpcAccount(target)) {
                String msg = TextUtils.ConvertTextColor("&4Couldn't get any account with " + target + " from database!!");
                if (commandSender.getName().equalsIgnoreCase("PLAYER")) {
                    commandSender.sendMessage(msg);
                }
                ConsoleLogger.severe(msg, ConsoleLogger.logTypes.log);
                return;
            }

            NonPlayerAccount nonPlayerAccount = tAM.getNpcAccount(target);

            nonPlayerAccount.retrieveBalance(currency,
                    new EconomySubscriber<BigDecimal>() {
                        @Override
                        public void succeed(@NotNull BigDecimal bigDecimal) {
                            amount[0] = bigDecimal;
                        }

                        @Override
                        public void fail(@NotNull EconomyException exception) {
                            commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't get non player account from database!!"));
                            ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
                        }
                    }
            );

            commandSender.sendMessage("NPC " + target + " balance is " + dataFormatter.formatBigDecimal(amount[0], false));

        }
        else {

            if (!tAM.hasPlayerAccount(playerID)) {
                String msg = TextUtils.ConvertTextColor("&4Couldn't get any account with " + target + " from database!!");
                if (commandSender.getName().equalsIgnoreCase("PLAYER")) {
                    commandSender.sendMessage(msg);
                }
                ConsoleLogger.severe(msg, ConsoleLogger.logTypes.log);
                return;
            }

            PlayerAccount playerAccount = tAM.getPlayerAccount(playerID);

            playerAccount.retrieveBalance(currency,
                    new EconomySubscriber<BigDecimal>() {
                        @Override
                        public void succeed(@NotNull BigDecimal bigDecimal) {
                            amount[0] = bigDecimal;
                        }

                        @Override
                        public void fail(@NotNull EconomyException exception) {
                            commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't get player account from database!!"));
                            ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
                        }
                    }
            );

            commandSender.sendMessage(target + " balance is " + dataFormatter.formatBigDecimal(amount[0], false));
        }

    }

    @CommandHook("baltop")
    public void baltop(CommandSender commandSender, CurrencyType currencyType) {
        commandSender.sendMessage("balother will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
    }

    // -------------------------------------------------------------------
    // ------------------[ NON PLAYER SECTION ]---------------------------
    // -------------------------------------------------------------------

    @CommandHook("npclist")
    public void npclist(CommandSender commandSender, CurrencyType currencyType) {
        commandSender.sendMessage("npclist will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
    }

    @CommandHook("npccreate")
    public void npccreate(CommandSender commandSender, CurrencyType currencyType) {
        commandSender.sendMessage("npccreate will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
    }

    @CommandHook("npcdelete")
    public void npcdelete(CommandSender commandSender, CurrencyType currencyType) {
        commandSender.sendMessage("npcdelete will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
    }

    @CommandHook("npcrename")
    public void npcrename(CommandSender commandSender, CurrencyType currencyType) {
        commandSender.sendMessage("npcrename will be implemented soon!");
        commandSender.sendMessage("Command Sender : " + commandSender.getName());
        commandSender.sendMessage("Currency Type : " + currencyType.getName());
    }

//    @CommandHook("baltop")
//    public void baltop(CommandSender commandSender, CurrencyType currencyType) {
//        commandSender.sendMessage("balother will be implemented soon!");
//        commandSender.sendMessage("Command Sender : " + commandSender.getName());
//        commandSender.sendMessage("Currency Type : " + currencyType.getName());
//    }

}
