package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.DatabaseManager;
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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.Messages;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class TreasuryPlayerAccount implements PlayerAccount {
    private final MultiCurrency instance;
    private final Player player;
    private final UUID uuid;
    private final EventBus eventBus = EventBus.INSTANCE;
    DatabaseManager dbm;
    TreasuryManager treasuryManager;

    public TreasuryPlayerAccount(MultiCurrency instance, UUID uuid){
        this.instance = instance;
        this.dbm = instance.getDBM();
        this.treasuryManager = instance.getTreasuryManager();
        this.uuid = uuid;
        this.player = (Player) Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public Optional<String> getName() {
        return Optional.of(player.getName());
    }

    @Override
    public void retrieveBalance(@NotNull Currency currency, @NotNull EconomySubscriber<BigDecimal> subscription) {
        BigDecimal value;
        DatabaseManager dbm = instance.getDBM();

        String currencyName = currency.getIdentifier();
        CurrencyType ctyp = treasuryManager.getCurrencyTypes().get(currencyName);

        if (!dbm.doesUserExists(player, ctyp)){
            dbm.createUser(player, ctyp);
        }

        value = new BigDecimal(dbm.getBalance(player, ctyp).toString());

        subscription.succeed(value);
    }

    @Override
    public void setBalance(@NotNull BigDecimal amount, @NotNull EconomyTransactionInitiator<?> initiator, @NotNull Currency currency, @NotNull EconomySubscriber<BigDecimal> subscription) {
        String currencyName = currency.getIdentifier();
        CurrencyType ctyp;

        if (!treasuryManager.getTreasuryCurrency().containsKey(currencyName)) {
            subscription.fail(new EconomyException(FailureReasons.INVALID_CURRENCY));
        }
        else {
            ctyp = treasuryManager.getCurrencyTypes().get(currencyName);
            BigDecimal fixedAmount = instance.getCurrencyTypeManager()
                                                .getCurrencyType(currencyName)
                                                .getDataFormatter()
                                                .formatdouble(amount);

            String formattedAmount = instance.getCurrencyTypeManager()
                    .getCurrencyType(currencyName)
                    .getDataFormatter()
                    .formatBigDecimal(fixedAmount);

            boolean status = dbm.updateBalance(player, ctyp, fixedAmount);

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
                player.sendMessage(playermsg);

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

                BigDecimal fixedAmount = instance.getCurrencyTypeManager()
                        .getCurrencyType(currencyName)
                        .getDataFormatter()
                        .formatdouble(amount);

                String formattedAmount = instance.getCurrencyTypeManager()
                        .getCurrencyType(currencyName)
                        .getDataFormatter()
                        .formatBigDecimal(fixedAmount);

                // check if the transaction is withdrawl or deposit
                BigDecimal previousAmount; // = BigDecimal.valueOf(0);
                BigDecimal newAmount; // = BigDecimal.valueOf(0);

                if (!dbm.doesUserExists(uuid, ctyp)) {
                    dbm.createUser(uuid, ctyp);
                }

                previousAmount = dbm.getBalance(uuid, ctyp);

                if (transactionType == EconomyTransactionType.WITHDRAWAL) {
                    if (previousAmount.signum() <= 0){
                        subscription.fail(new EconomyException(FailureReasons.BALANCE_NOT_ENOUGH));
                    }
                    else if (previousAmount.subtract(fixedAmount).signum() == -1) {
                        subscription.fail(new EconomyException(FailureReasons.BALANCE_NOT_ENOUGH));
                    }

                    newAmount = previousAmount.subtract(fixedAmount);
                }
                else {
                    newAmount = previousAmount.add(fixedAmount);
                }

                boolean status = dbm.updateBalance(player, ctyp, newAmount);

                if (status) {
                    EconomyTransactionInitiator.Type type = initiator.getType();
                    String consolemsg;
                    String playermsg;

                    if (type == EconomyTransactionInitiator.Type.PLAYER) {
                        UUID initiatorPlayerID = (UUID) initiator.getData();
                        Player initiatorPlayer = (Player) Bukkit.getOfflinePlayer(initiatorPlayerID);
                        consolemsg = "Player " + initiatorPlayer.getName() + " has given " + player.getName() + " " + formattedAmount;
                        playermsg = Messages.msg("prefix") + " Player " + initiatorPlayer.getName()+ " has given you " + formattedAmount;
                    }
                    else if (type == EconomyTransactionInitiator.Type.PLUGIN) {
                        String pluginname = (String) initiator.getData();
                        consolemsg = "Plugin " + pluginname + " has given " + player.getName() + " " + formattedAmount;
                        playermsg = Messages.msg("prefix") + " You got " + formattedAmount;
                    }
                    else {
                        consolemsg = "Server has given " + player.getName() + " " + formattedAmount;
                        playermsg = Messages.msg("prefix") + " You got " + formattedAmount;
                    }


                    ConsoleLogger.info(consolemsg, ConsoleLogger.logTypes.log);
                    player.sendMessage(playermsg);

                    if (ctyp.logTransactionEnabled()) {
                        // to be added logging ignore the below command
                        ConsoleLogger.info(consolemsg, ConsoleLogger.logTypes.log);
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

    }

    @Override
    public void retrieveHeldCurrencies(@NotNull EconomySubscriber<Collection<String>> subscription) {

    }

    @Override
    public void retrieveTransactionHistory(int transactionCount, @NotNull Temporal from, @NotNull Temporal to, @NotNull EconomySubscriber<Collection<EconomyTransaction>> subscription) {

    }
}
