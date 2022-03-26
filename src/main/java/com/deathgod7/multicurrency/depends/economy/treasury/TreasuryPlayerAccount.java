package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.DataFormatter;
import com.deathgod7.multicurrency.data.DatabaseManager;
import com.deathgod7.multicurrency.data.helper.Column;
import com.deathgod7.multicurrency.data.helper.Table;
import com.deathgod7.multicurrency.data.helper.TransactionTable;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import me.lokka30.treasury.api.common.event.EventBus;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.events.PlayerAccountTransactionEvent;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import me.lokka30.treasury.api.economy.transaction.EconomyTransaction;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.Messages;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

public class TreasuryPlayerAccount implements PlayerAccount {
    private final MultiCurrency instance;
    private final OfflinePlayer player;
    private final UUID uuid;
    private final EventBus eventBus = EventBus.INSTANCE;
    DatabaseManager dbm;
    TreasuryManager treasuryManager;

    public TreasuryPlayerAccount(MultiCurrency instance, UUID uuid){
        this.instance = instance;
        this.dbm = instance.getDBM();
        this.treasuryManager = instance.getTreasuryManager();
        this.uuid = uuid;
        this.player = Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(player.getName());
    }

    @Override
    public void retrieveBalance(@NotNull Currency currency, @NotNull EconomySubscriber<BigDecimal> subscription) {
        BigDecimal value;
        DatabaseManager dbm = instance.getDBM();

        String currencyName = currency.getIdentifier();
        CurrencyType ctyp = treasuryManager.getCurrencyTypes().get(currencyName);

        if (!dbm.doesUserExists(uuid, ctyp)){
            dbm.createUser(uuid, ctyp);
        }

        value = dbm.getBalance(uuid, ctyp);

        if (value != null) {
            subscription.succeed(value);
        }
        else {
            subscription.fail(new EconomyException(FailureReasons.ACCOUNTS_RETRIEVE_FAILURE));
        }
    }

    @Override
    public void setBalance(@NotNull BigDecimal amount, @NotNull EconomyTransactionInitiator<?> initiator, @NotNull Currency currency, @NotNull EconomySubscriber<BigDecimal> subscription) {
        String currencyName = currency.getIdentifier();
        CurrencyType ctyp;
        DataFormatter dataFormatter = instance.getCurrencyTypeManager().getCurrencyType(currencyName).getDataFormatter();

        if (!treasuryManager.getTreasuryCurrency().containsKey(currencyName)) {
            subscription.fail(new EconomyException(FailureReasons.INVALID_CURRENCY));
        }
        else {
            ctyp = treasuryManager.getCurrencyTypes().get(currencyName);
            BigDecimal fixedAmount = dataFormatter.parseBigDecimal(amount);

            String formattedAmount = dataFormatter.formatBigDecimal(fixedAmount, true);

            boolean status = dbm.updateBalance(uuid, ctyp, fixedAmount);

            if (status) {
                EconomyTransactionInitiator.Type type = initiator.getType();
                String consolemsg;
                String playermsg;
                if (type == EconomyTransactionInitiator.Type.PLAYER) {
                    UUID initiatorPlayerID = (UUID) initiator.getData();
                    Player initiatorPlayer = (Player) Bukkit.getOfflinePlayer(initiatorPlayerID);
                    consolemsg = "Player " + initiatorPlayer.getName() + " has set balance for " + player.getName() + " to " + formattedAmount;
                }
                else if (type == EconomyTransactionInitiator.Type.PLUGIN) {
                    String pluginname = (String) initiator.getData();
                    consolemsg = "Plugin " + pluginname + " has set balance for " + player.getName() + " to " + formattedAmount;
                }
                else {
                    consolemsg = "Server has set balance for " + player.getName() + " to " + formattedAmount;
                }

                playermsg = Messages.msg("prefix") + " Your balance has been updated to " + formattedAmount;

                ConsoleLogger.info(consolemsg, ConsoleLogger.logTypes.log);
                if (player.isOnline()) {
                    Objects.requireNonNull(player.getPlayer()).sendMessage(playermsg);
                }

                subscription.succeed(fixedAmount);

            }
            else {
                subscription.fail(new EconomyException(FailureReasons.UPDATE_FAILED));
            }
        }

    }

    @Override
    public void doTransaction(@NotNull EconomyTransaction economyTransaction, EconomySubscriber<BigDecimal> subscription) {
        String currencyName = economyTransaction.getCurrencyID();
        String transactionReason = String.valueOf(economyTransaction.getReason());
        String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(economyTransaction.getTimestamp());
        BigDecimal amount = economyTransaction.getTransactionAmount();
        EconomyTransactionType transactionType = economyTransaction.getTransactionType();
        EconomyTransactionInitiator<?> initiator = economyTransaction.getInitiator();

        DataFormatter dataFormatter = instance.getCurrencyTypeManager().getCurrencyType(currencyName).getDataFormatter();

        CurrencyType ctyp;

        if (!treasuryManager.getTreasuryCurrency().containsKey(currencyName)) {
            subscription.fail(new EconomyException(FailureReasons.INVALID_CURRENCY));
        }
        else {
            PlayerAccountTransactionEvent event = new PlayerAccountTransactionEvent(economyTransaction, this);
            eventBus.fire(event);

            if (event.isCancelled()){
                ConsoleLogger.severe("Couldn't do the transaction.", ConsoleLogger.logTypes.debug);
            }
            else {
                ctyp = treasuryManager.getCurrencyTypes().get(currencyName);

                if (amount.signum() <= 0) {
                    subscription.fail(new EconomyException(FailureReasons.INVALID_VALUE));
                }

                BigDecimal fixedAmount = dataFormatter.parseBigDecimal(amount);

                String formattedAmount = dataFormatter.formatBigDecimal(fixedAmount, true);

                // check if the transaction is withdrawl or deposit
                BigDecimal previousAmount; // = BigDecimal.valueOf(0);
                BigDecimal newAmount; // = BigDecimal.valueOf(0);

                if (!dbm.doesUserExists(uuid, ctyp)) {
                    dbm.createUser(uuid, ctyp);
                }

                previousAmount = dbm.getBalance(uuid, ctyp);
                boolean isWithdrawal = false;

                if (transactionType == EconomyTransactionType.WITHDRAWAL) {
                    if (previousAmount.signum() <= 0){
                        subscription.fail(new EconomyException(FailureReasons.BALANCE_NOT_ENOUGH));
                    }
                    else if (previousAmount.subtract(fixedAmount).signum() == -1) {
                        subscription.fail(new EconomyException(FailureReasons.BALANCE_NOT_ENOUGH));
                    }

                    newAmount = previousAmount.subtract(fixedAmount);
                    isWithdrawal = true;
                }
                else {
                    newAmount = previousAmount.add(fixedAmount);
                }

                boolean status = dbm.updateBalance(uuid, ctyp, newAmount);

                if (status) {
                    EconomyTransactionInitiator.Type type = initiator.getType();
                    String consolemsg;
                    String initiatormsg;
                    String accountholdermsg;
                    String transactionFrom;
                    String transactionTypeFormatted;

                    if (type == EconomyTransactionInitiator.Type.PLAYER) {
                        UUID initiatorPlayerID = (UUID) initiator.getData();
                        Player initiatorPlayer = (Player) Bukkit.getOfflinePlayer(initiatorPlayerID);
                        transactionTypeFormatted = "PLAYER";
                        transactionFrom = initiatorPlayer.getName();
                        consolemsg = "Player " + initiatorPlayer.getName() + " has given " + player.getName() + " " + formattedAmount;

                        if (!isWithdrawal) {
                            initiatormsg = Messages.msg("prefix") + " You have given player " + initiatorPlayer.getName()+ " " + formattedAmount;
                            accountholdermsg = Messages.msg("prefix") + " Player " + initiatorPlayer.getName()+ " has given you " + formattedAmount;
                            initiatorPlayer.sendMessage(initiatormsg);
                        }
                        else {
                            accountholdermsg = Messages.msg("prefix") + " Your account has been deducted by " + formattedAmount;
                        }
                    }
                    else if (type == EconomyTransactionInitiator.Type.PLUGIN) {
                        String pluginname = (String) initiator.getData();
                        transactionTypeFormatted = "PLUGIN";
                        transactionFrom = pluginname;
                        consolemsg = "Plugin " + pluginname + " has given " + player.getName() + " " + formattedAmount;
                        accountholdermsg = Messages.msg("prefix") + " You got " + formattedAmount;
                    }
                    else {
                        transactionTypeFormatted = "SERVER";
                        transactionFrom = "Server";
                        consolemsg = "Server has given " + player.getName() + " " + formattedAmount;
                        accountholdermsg = Messages.msg("prefix") + " You got " + formattedAmount;
                    }


                    ConsoleLogger.info(consolemsg, ConsoleLogger.logTypes.log);

                    if (player.isOnline()) {
                        Objects.requireNonNull(player.getPlayer()).sendMessage(accountholdermsg);
                    }

                    if (ctyp.logTransactionEnabled()) {
                        Table transactionsTable = dbm.getTables().get("Transactions");
                        if (transactionsTable != null) {
                            List<Column> temp = TransactionTable.TransactionData(timestamp, currencyName,
                                    newAmount.toString(), transactionTypeFormatted, transactionFrom,
                                    player.getName(), transactionReason);

                            // put in db
                            transactionsTable.insert(temp);

                            ConsoleLogger.info("Logged the transaction in database.", ConsoleLogger.logTypes.debug);


                        }
                        else {
                            ConsoleLogger.severe("Couldn't log the transaction in database. Please check if you have configured db correctly.", ConsoleLogger.logTypes.debug);
                        }
                    }

                    subscription.succeed(newAmount);

                }
                else {
                    subscription.fail(new EconomyException(FailureReasons.UPDATE_FAILED));
                }
            }

        }

    }

    @Override
    public void deleteAccount(@NotNull EconomySubscriber<Boolean> subscription) {
        // to be added
    }

    @Override
    public void retrieveHeldCurrencies(@NotNull EconomySubscriber<Collection<String>> subscription) {
        List<String> heldCurrencies = new ArrayList<>();

        for (CurrencyType x : instance.getTreasuryManager().getCurrencyTypes().values()) {
            if (dbm.doesUserExists(uuid, x)) {
                if (instance.getTreasuryManager().getTreasuryCurrency().containsKey(x.getName())) {
                    heldCurrencies.add(x.getName());
                }
                else {
                    ConsoleLogger.info("Currency not loaded : " + x.getName(), ConsoleLogger.logTypes.debug);
                }
            }
            else {
                ConsoleLogger.info("User not in table of currency : " + x.getName(), ConsoleLogger.logTypes.debug);
            }
        }

        subscription.succeed(heldCurrencies);

    }

    @Override
    public void retrieveTransactionHistory(int transactionCount, @NotNull Temporal from, @NotNull Temporal to, @NotNull EconomySubscriber<Collection<EconomyTransaction>> subscription) {
        // to be added (after i verify transaction are stored in db)
    }
}
