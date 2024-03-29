package com.github.deathgod7.multicurrency.depends.economy.treasury;

import com.github.deathgod7.multicurrency.MultiCurrency;
import me.lokka30.treasury.api.economy.EconomyProvider;
import me.lokka30.treasury.api.economy.account.Account;
import me.lokka30.treasury.api.economy.account.NonPlayerAccount;
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
	TreasuryManager treasuryManager;

	TreasuryAccountManager treasuryAccountManager;

	public TreasuryHook(MultiCurrency instance){
		this.instance = instance;
		this.treasuryManager = instance.getTreasuryManager();
		this.treasuryAccountManager = instance.getTreasuryAccountmanager();
	}

	@Override
	public @NotNull Set<OptionalEconomyApiFeature> getSupportedOptionalEconomyApiFeatures() {
		Set<OptionalEconomyApiFeature> features = new HashSet<>();
		//features.add(OptionalEconomyApiFeature.NEGATIVE_BALANCES);
		features.add(OptionalEconomyApiFeature.TRANSACTION_EVENTS);
		return features;
	}

	@Override
	public void hasPlayerAccount(@NotNull UUID accountId, @NotNull EconomySubscriber<Boolean> subscription) {
		subscription.succeed(treasuryAccountManager.hasPlayerAccount(accountId)
		);
	}

	@Override
	public void retrievePlayerAccount(@NotNull UUID accountId, @NotNull EconomySubscriber<PlayerAccount> subscription) {
		PlayerAccount playerAccount =  treasuryAccountManager.getPlayerAccount(accountId);

		if (playerAccount != null){
			subscription.succeed(playerAccount);
		}
		else {
			subscription.fail(new EconomyException(FailureReasons.ACCOUNT_NOT_FOUND));
		}

	}

	@Override
	public void createPlayerAccount(@NotNull UUID accountId, @NotNull EconomySubscriber<PlayerAccount> subscription) {
		PlayerAccount playerAccount = treasuryAccountManager.registerPlayerAccount(accountId);

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

		HashMap<String, PlayerAccount> temp = treasuryAccountManager.getAllPlayerAccounts();

		for (String x : temp.keySet()) {
			uuidList.add(UUID.fromString(x));
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
		boolean status = treasuryAccountManager.hasNpcAccount(identifier);

		subscription.succeed(status);
	}

	@Override
	public void retrieveAccount(@NotNull String identifier, @NotNull EconomySubscriber<Account> subscription) {
		NonPlayerAccount nonPlayerAccount = treasuryAccountManager.getNpcAccount(identifier);

		if (nonPlayerAccount != null){
			subscription.succeed(nonPlayerAccount);
		}
		else {
			subscription.fail(new EconomyException(FailureReasons.ACCOUNT_NOT_FOUND));
		}

	}

	@Override
	public void createAccount(@Nullable String name, @NotNull String identifier, @NotNull EconomySubscriber<Account> subscription) {
		NonPlayerAccount nonPlayerAccount = treasuryAccountManager.registerNpcAccount(identifier, name);

		if (nonPlayerAccount != null){
			subscription.succeed(nonPlayerAccount);
		}
		else {
			subscription.fail(new EconomyException(FailureReasons.ACCOUNT_CREATE_FAILURE));
		}
	}

	@Override
	public void retrieveNonPlayerAccountIds(@NotNull EconomySubscriber<Collection<String>> subscription) {

		HashMap<String, NonPlayerAccount> temp = treasuryAccountManager.getAllNpcAccounts();

		List<String> accountIds = new ArrayList<>(temp.keySet());

		if (!accountIds.isEmpty()){
			subscription.succeed(accountIds);
		}
		else {
			subscription.fail(new EconomyException(FailureReasons.ACCOUNTS_RETRIEVE_FAILURE));
		}
	}

	// for retrieving all accounts
	@Override
	public void retrieveAccountIds(@NotNull EconomySubscriber<Collection<String>> subscription) {
		List<String> accountIds = new ArrayList<>();

		HashMap<String, PlayerAccount> temp = treasuryAccountManager.getAllPlayerAccounts();
		HashMap<String, NonPlayerAccount> temp1 = treasuryAccountManager.getAllNpcAccounts();

		accountIds.addAll(temp.keySet());
		accountIds.addAll(temp1.keySet());

		if (!accountIds.isEmpty()){
			subscription.succeed(accountIds);
		}
		else {
			subscription.fail(new EconomyException(FailureReasons.ACCOUNTS_RETRIEVE_FAILURE));
		}

	}


	// all overall methods.... sike
	@Override
	public @NotNull Currency getPrimaryCurrency() {
		String primaryCurrency = instance.getMainConfig().primary_currency;
		return treasuryManager.getTreasuryCurrency().get(primaryCurrency);
	}

	@Override
	public Optional<Currency> findCurrency(@NotNull String identifier) {
		return Optional.ofNullable(treasuryManager.getTreasuryCurrency().get(identifier));
	}

	@Override
	public Set<Currency> getCurrencies() {
		return new HashSet<>(treasuryManager.getTreasuryCurrency().values());
	}

	@Override
	public void registerCurrency(@NotNull Currency currency, @NotNull EconomySubscriber<Boolean> subscription) {
		subscription.fail(new EconomyException(FailureReasons.FEATURE_NOT_SUPPORTED));
	}
}
