package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import me.lokka30.treasury.api.common.misc.TriState;
import me.lokka30.treasury.api.economy.account.Account;
import me.lokka30.treasury.api.economy.account.AccountPermission;
import me.lokka30.treasury.api.economy.account.NonPlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import me.lokka30.treasury.api.economy.transaction.EconomyTransaction;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TreasuryNpcAccount implements NonPlayerAccount {
    MultiCurrency instance;
    String accountname;
    String identifier;

    public TreasuryNpcAccount(MultiCurrency instance, String identifier, String accountname) {
        this.instance = instance;
        this.accountname = accountname;
        this.identifier = identifier;
    }

    @Override
    public @NotNull String getIdentifier() {
        return null;
    }

    @Override
    public Optional<String> getName() {
        return Optional.empty();
    }

    @Override
    public void setName(@Nullable String name, @NotNull EconomySubscriber<Boolean> subscription) {

    }

    @Override
    public void retrieveBalance(@NotNull Currency currency, @NotNull EconomySubscriber<BigDecimal> subscription) {

    }

    @Override
    public void setBalance(@NotNull BigDecimal amount, @NotNull EconomyTransactionInitiator<?> initiator, @NotNull Currency currency, @NotNull EconomySubscriber<BigDecimal> subscription) {

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

    @Override
    public void retrieveMemberIds(@NotNull EconomySubscriber<Collection<UUID>> subscription) {

    }

    @Override
    public void isMember(@NotNull UUID player, @NotNull EconomySubscriber<Boolean> subscription) {

    }

    @Override
    public void setPermission(@NotNull UUID player, @NotNull TriState permissionValue, @NotNull EconomySubscriber<TriState> subscription, @NotNull AccountPermission @NotNull ... permissions) {

    }

    @Override
    public void retrievePermissions(@NotNull UUID player, @NotNull EconomySubscriber<Map<AccountPermission, TriState>> subscription) {

    }

    @Override
    public void hasPermission(@NotNull UUID player, @NotNull EconomySubscriber<TriState> subscription, @NotNull AccountPermission @NotNull ... permissions) {

    }
}
