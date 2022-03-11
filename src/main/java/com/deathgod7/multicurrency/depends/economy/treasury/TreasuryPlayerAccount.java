package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.DatabaseManager;
import com.deathgod7.multicurrency.data.helper.Table;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import com.deathgod7.multicurrency.utils.TextUtils;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import me.lokka30.treasury.api.economy.transaction.EconomyTransaction;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class TreasuryPlayerAccount implements PlayerAccount {
    private final MultiCurrency instance;
    private final Player player;
    private final UUID uuid;

    public TreasuryPlayerAccount(MultiCurrency instance, UUID uuid){
        this.instance = instance;
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

        String currencyName = TextUtils.GetCurrencyName(currency.getIdentifier());
        CurrencyType ctyp = TreasuryManager.currencyTypes.get(currencyName);

        if (!dbm.doesUserExists(player, ctyp)){
            dbm.createUser(player, ctyp);
        }

        value = new BigDecimal(dbm.getBalance(player, ctyp).toString());

        subscription.succeed(value);
    }

    @Override
    public void setBalance(@NotNull BigDecimal amount, @NotNull EconomyTransactionInitiator<?> initiator, @NotNull Currency currency, @NotNull EconomySubscriber<BigDecimal> subscription) {
        String currencyName = TextUtils.GetCurrencyName(currency.getIdentifier());
        DatabaseManager dbm = instance.getDBM();
        CurrencyType ctyp;

        if (!TreasuryManager.treasuryCurrency.containsKey(currencyName)) {
            subscription.fail(new EconomyException(FailureReasons.INVALID_CURRENCY));
        }
        else {
            ctyp = TreasuryManager.currencyTypes.get(currencyName);
            boolean status = dbm.updateBalance(player, ctyp, amount);

            if (status) {
                EconomyTransactionInitiator.Type type = initiator.getType();
                String consolemsg;
                String playermsg;
                if (type == EconomyTransactionInitiator.Type.PLAYER) {
                    consolemsg = type.toString() + " has set balance for " + player.getName() + " to " + amount.toString())
                    playermsg = initiator.getData().
                }
                else if (type == EconomyTransactionInitiator.Type.SERVER) {

                }
                else {

                }
                ConsoleLogger.info(.toString() + " set balance for ");
            }
            else {

            }
        }
    }

    @Override
    public void doTransaction(@NotNull EconomyTransaction economyTransaction, EconomySubscriber<BigDecimal> subscription) {

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
