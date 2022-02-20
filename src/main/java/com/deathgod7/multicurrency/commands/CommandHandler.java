package com.deathgod7.multicurrency.commands;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.depends.economy.CurrencyTypes;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;

import java.util.Objects;

import static com.deathgod7.multicurrency.utils.TextUtils.ConvertTextColor;

public class CommandHandler {

    private final MultiCurrency multiCurrency;

    public CommandHandler(MultiCurrency multiCurrency) {
        this.multiCurrency = multiCurrency;
    }

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

        ConsoleLogger.info("Multi Currency", ConsoleLogger.logTypes.log);
        ConsoleLogger.info("Version : " + version, ConsoleLogger.logTypes.log);
        ConsoleLogger.info("Developer(s) : " + developer, ConsoleLogger.logTypes.log);
        ConsoleLogger.info("API Version : " + apiversion, ConsoleLogger.logTypes.log);
        ConsoleLogger.info("Database : " + databaseType, ConsoleLogger.logTypes.log);
        ConsoleLogger.info("Status : " + isConnected, ConsoleLogger.logTypes.log);
        MultiCurrency.getInstance().getDbm().loadSqliteTable();
    }

//    @CommandHook("add")
//    public void add(CommandSender commandSender, Player target, CurrencyTypes currencyTypes, int amount, boolean isSilent) {
//        int currentBal = currencyTypes.
//
//        if (currencyTypes.getMaxBal()) {
//            commandSender.sendMessage(ChatColor.RED + "User already has a mine!");
//            return;
//        }
//        commandSender.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a private mine!");
//        Location location = mineWorldManager.getNextFreeLocation();
//        final MineType defaultMineType = privateMines.getMineTypeManager().getDefaultMineType();
//        privateMines.getLogger().info(defaultMineType.getName());
//
//        if (mineType == null) {
//            mineFactory.createMine(target, location, defaultMineType, false);
//        } else {
//            mineFactory.createMine(target, location, mineType, false);
//        }
//    }

}
