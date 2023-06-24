package com.github.deathgod7.multicurrency.depends.economy.treasury;

import com.github.deathgod7.multicurrency.MultiCurrency;
import com.github.deathgod7.multicurrency.data.DataFormatter;
import com.github.deathgod7.multicurrency.data.DatabaseManager;
import com.github.deathgod7.multicurrency.data.helper.Column;
import com.github.deathgod7.multicurrency.data.helper.Table;
import com.github.deathgod7.multicurrency.data.helper.TransactionTable;
import com.github.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.github.deathgod7.multicurrency.utils.ConsoleLogger;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
		String transactionReason = "Update Balance";
		String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS").withZone(ZoneId.systemDefault()).format(LocalDateTime.now());
		String transactionTypeFormatted = "Update";

		CurrencyType ctyp;
		DataFormatter dataFormatter;

		if (!treasuryManager.getTreasuryCurrency().containsKey(currencyName)) {
			subscription.fail(new EconomyException(FailureReasons.INVALID_CURRENCY));
		}
		else {
			ctyp = treasuryManager.getCurrencyTypes().get(currencyName);
			dataFormatter = new DataFormatter(ctyp);

			BigDecimal fixedAmount = dataFormatter.parseBigDecimal(amount);

			//String formattedAmount = dataFormatter.formatBigDecimal(fixedAmount, true);

			boolean status = dbm.updateBalance(uuid, ctyp, fixedAmount);

			if (status) {
				EconomyTransactionInitiator.Type type = initiator.getType();
				String transactionFrom;

				if (type == EconomyTransactionInitiator.Type.PLAYER) {
					UUID initiatorPlayerID = (UUID) initiator.getData();
					Player initiatorPlayer = (Player) Bukkit.getOfflinePlayer(initiatorPlayerID);
					transactionFrom = initiatorPlayer.getName();
				}
				else if (type == EconomyTransactionInitiator.Type.PLUGIN) {
					transactionFrom = (String) initiator.getData();
				}
				else {
					transactionFrom = "Server";
				}


				if (ctyp.logTransactionEnabled()) {
					Table transactionsTable = dbm.getTables().get("Transactions");
					if (transactionsTable != null) {
						List<Column> temp = TransactionTable.TransactionData(timestamp, currencyName,
								fixedAmount.toString(), transactionTypeFormatted, transactionFrom,
								player.getName(), transactionReason);

						// put in db
						boolean up = transactionsTable.insert(temp);

						if (up) {
							ConsoleLogger.info("From : " + transactionFrom + " To : " + player.getName(), ConsoleLogger.logTypes.debug);
							ConsoleLogger.info("Type : " + transactionTypeFormatted, ConsoleLogger.logTypes.debug);
							ConsoleLogger.info("Money : " + fixedAmount + " (" + currencyName + ")" + player.getName(), ConsoleLogger.logTypes.debug);
							ConsoleLogger.info("Reason : " + transactionReason, ConsoleLogger.logTypes.debug);
							ConsoleLogger.info("Logged the transaction in database.", ConsoleLogger.logTypes.debug);
						}
						else {
							ConsoleLogger.info("Transaction logs not updated......hmmmmm", ConsoleLogger.logTypes.debug);
						}


					}
					else {
						ConsoleLogger.severe("Couldn't log the transaction in database. Please check if you have configured db correctly.", ConsoleLogger.logTypes.debug);
					}
				}


				subscription.succeed(fixedAmount);

			}
			else {
				subscription.fail(new EconomyException(FailureReasons.UPDATE_FAILED));
			}
		}

	}

	@Override
	public void doTransaction(@NotNull EconomyTransaction economyTransaction, @NotNull EconomySubscriber<BigDecimal> subscription) {
		String currencyName = economyTransaction.getCurrencyID();
		String transactionReason = economyTransaction.getReason().isPresent() ? economyTransaction.getReason().get() : "";
		String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS").withZone(ZoneId.systemDefault()).format(economyTransaction.getTimestamp());
		BigDecimal amount = economyTransaction.getTransactionAmount();
		EconomyTransactionType transactionType = economyTransaction.getTransactionType();
		EconomyTransactionInitiator<?> initiator = economyTransaction.getInitiator();

		DataFormatter dataFormatter;
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
				dataFormatter = new DataFormatter(ctyp);

				if (amount.signum() <= 0) {
					subscription.fail(new EconomyException(FailureReasons.INVALID_VALUE));
				}

				BigDecimal fixedAmount = dataFormatter.parseBigDecimal(amount);

				//String formattedAmount = dataFormatter.formatBigDecimal(fixedAmount, true);

				// check if the transaction is withdrawl or deposit
				BigDecimal previousAmount; // = BigDecimal.valueOf(0);
				BigDecimal newAmount; // = BigDecimal.valueOf(0);

				if (!dbm.doesUserExists(uuid, ctyp)) {
					dbm.createUser(uuid, ctyp);
				}

				previousAmount = dbm.getBalance(uuid, ctyp);
				String transactionTypeFormatted;

				if (transactionType == EconomyTransactionType.WITHDRAWAL) {
					if (previousAmount.signum() <= 0){
						subscription.fail(new EconomyException(FailureReasons.BALANCE_NOT_ENOUGH));
					}
					else if (previousAmount.subtract(fixedAmount).signum() == -1) {
						subscription.fail(new EconomyException(FailureReasons.BALANCE_NOT_ENOUGH));
					}

					newAmount = previousAmount.subtract(fixedAmount);
					transactionTypeFormatted = "Withdrawal";
				}
				else {
					newAmount = previousAmount.add(fixedAmount);
					transactionTypeFormatted = "Deposit";
				}

				boolean status = dbm.updateBalance(uuid, ctyp, newAmount);

				if (status) {
					EconomyTransactionInitiator.Type type = initiator.getType();
					String transactionFrom;

					if (type == EconomyTransactionInitiator.Type.PLAYER) {
						UUID initiatorPlayerID = (UUID) initiator.getData();
						Player initiatorPlayer = (Player) Bukkit.getOfflinePlayer(initiatorPlayerID);
						transactionFrom = initiatorPlayer.getName();
					}
					else if (type == EconomyTransactionInitiator.Type.PLUGIN) {
						transactionFrom = (String) initiator.getData();
					}
					else {
						transactionFrom = "Server";
					}



					if (ctyp.logTransactionEnabled()) {
						Table transactionsTable = dbm.getTables().get("Transactions");
						if (transactionsTable != null) {
							List<Column> temp = TransactionTable.TransactionData(timestamp, currencyName,
									fixedAmount.toString(), transactionTypeFormatted, transactionFrom,
									player.getName(), transactionReason);

							// put in db
							boolean up = transactionsTable.insert(temp);

							if (up) {
								ConsoleLogger.info("From : " + transactionFrom + " To : " + player.getName(), ConsoleLogger.logTypes.debug);
								ConsoleLogger.info("Type : " + transactionTypeFormatted, ConsoleLogger.logTypes.debug);
								ConsoleLogger.info("Money : " + fixedAmount + " (" + currencyName + ")" + player.getName(), ConsoleLogger.logTypes.debug);
								ConsoleLogger.info("Reason : " + transactionReason, ConsoleLogger.logTypes.debug);
								ConsoleLogger.info("Logged the transaction in database.", ConsoleLogger.logTypes.debug);
							}
							else {
								ConsoleLogger.info("Transaction logs not updated......hmmmmm", ConsoleLogger.logTypes.debug);
							}


						}
						else {
							ConsoleLogger.severe("Couldn't log the transaction in database. Please check if you have configured db correctly.", ConsoleLogger.logTypes.debug);
						}
					}

					subscription.succeed(fixedAmount);

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
