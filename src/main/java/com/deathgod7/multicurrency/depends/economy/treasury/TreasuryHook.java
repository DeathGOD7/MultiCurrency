package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import me.lokka30.treasury.api.economy.EconomyProvider;
import me.lokka30.treasury.api.economy.account.Account;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.misc.OptionalEconomyApiFeature;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TreasuryHook implements EconomyProvider {

    MultiCurrency instance;

    public TreasuryHook(MultiCurrency instance){
        this.instance = instance;
    }

    @Override
    public @NotNull Set<OptionalEconomyApiFeature> getSupportedOptionalEconomyApiFeatures() {
        return Collections.emptySet();
    }

    @Override
    public void hasPlayerAccount(@NotNull UUID accountId, @NotNull EconomySubscriber<Boolean> subscription) {
        boolean status = instance.getTreasuryManager().getTreasuryAccountmanager().hasPlayerAccount(accountId);

        subscription.succeed(status);
    }

    @Override
    public void retrievePlayerAccount(@NotNull UUID accountId, @NotNull EconomySubscriber<PlayerAccount> subscription) {
        PlayerAccount playerAccount =  instance.getTreasuryManager().getTreasuryAccountmanager().getPlayerAccount(accountId);

        if (playerAccount != null){
            subscription.succeed(playerAccount);
        }
        else {
            subscription.fail(new EconomyException(FailureReasons.ACCOUNT_NOT_FOUND));
        }

    }

    @Override
    public void createPlayerAccount(@NotNull UUID accountId, @NotNull EconomySubscriber<PlayerAccount> subscription) {
        PlayerAccount playerAccount = instance.getTreasuryManager().getTreasuryAccountmanager().registerPlayerAccount(accountId);

        if (playerAccount != null){
            subscription.succeed(playerAccount);
        }
        else {
            subscription.fail(new EconomyException(FailureReasons.ACCOUNT_CREATE_FAILURE));
        }
    }

    @Override
    public void retrievePlayerAccountIds(@NotNull EconomySubscriber<Collection<UUID>> subscription) {
        List<UUID> uuidList = new ArrayList<>();

        HashMap<String, PlayerAccount> temp = instance.getTreasuryManager().getTreasuryAccountmanager().getAllPlayerAccounts();

        for (String x : temp.keySet()) {
            uuidList.add(temp.get(x).getUniqueId());
        }

        if (!uuidList.isEmpty()){
            subscription.succeed(uuidList);
        }
        else {
            subscription.fail(new EconomyException(FailureReasons.ACCOUNTS_RETRIEVE_FAILURE));
        }

    }

    // non player account uses "hasAccount" method
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
        String primaryCurrency = instance.getMainConfig().primary_currency;
        return TreasuryManager.treasuryCurrency.get(primaryCurrency);
    }

    @Override
    public Optional<Currency> findCurrency(@NotNull String identifier) {
        return Optional.ofNullable(TreasuryManager.treasuryCurrency.get(identifier));
    }

    @Override
    public Set<Currency> getCurrencies() {
        return new HashSet<>(TreasuryManager.treasuryCurrency.values());
    }

    @Override
    public void registerCurrency(@NotNull Currency currency, @NotNull EconomySubscriber<Boolean> subscription) {

    }
}
