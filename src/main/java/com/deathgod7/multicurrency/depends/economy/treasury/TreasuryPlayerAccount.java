package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
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
    private final MultiCurrency multiCurrency;
    private final Player player;
    private final UUID uuid;

    public TreasuryPlayerAccount(MultiCurrency instance, UUID uuid){
        this.multiCurrency = instance;
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
}
