package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import me.lokka30.treasury.api.economy.EconomyProvider;
import me.lokka30.treasury.api.economy.account.Account;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.misc.OptionalEconomyApiFeature;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TreasuryHook implements EconomyProvider {

    Currency currency;
    public Currency getCurrency(){
        return currency;
    }

    public TreasuryHook(Currency currency){
        this.currency = currency;
    }

    @Override
    public @NotNull Set<OptionalEconomyApiFeature> getSupportedOptionalEconomyApiFeatures() {
        return Collections.emptySet();
    }

    @Override
    public void hasPlayerAccount(@NotNull UUID accountId, @NotNull EconomySubscriber<Boolean> subscription) {
        boolean status = false;
        CurrencyType ctyp = TreasuryManager.currencyTypes.get(currency.getIdentifier().replace("Multi Currency - ", ""));

        if (ctyp != null) {
            status = MultiCurrency.getInstance().getDbm().doesUserExists(accountId, ctyp);
        }

        subscription.succeed(status);

    }

    @Override
    public void retrievePlayerAccount(@NotNull UUID accountId, @NotNull EconomySubscriber<PlayerAccount> subscription) {

    }

    @Override
    public void createPlayerAccount(@NotNull UUID accountId, @NotNull EconomySubscriber<PlayerAccount> subscription) {

    }

    @Override
    public void retrievePlayerAccountIds(@NotNull EconomySubscriber<Collection<UUID>> subscription) {

    }

    @Override
    public void hasAccount(@NotNull String identifier, @NotNull EconomySubscriber<Boolean> subscription) {

    }

    @Override
    public void retrieveAccount(@NotNull String identifier, @NotNull EconomySubscriber<Account> subscription) {

    }

    @Override
    public void createAccount(@Nullable String name, @NotNull String identifier, @NotNull EconomySubscriber<Account> subscription) {

    }

    @Override
    public void retrieveAccountIds(@NotNull EconomySubscriber<Collection<String>> subscription) {

    }

    @Override
    public void retrieveNonPlayerAccountIds(@NotNull EconomySubscriber<Collection<String>> subscription) {

    }

    @Override
    public @NotNull Currency getPrimaryCurrency() {
        return null;
    }

    @Override
    public Optional<Currency> findCurrency(@NotNull String identifier) {
        return Optional.empty();
    }

    @Override
    public Set<Currency> getCurrencies() {
        return null;
    }

    @Override
    public void registerCurrency(@NotNull Currency currency, @NotNull EconomySubscriber<Boolean> subscription) {

    }
}
