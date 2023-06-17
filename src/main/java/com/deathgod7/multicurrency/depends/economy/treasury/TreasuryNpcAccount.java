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
import me.lokka30.treasury.api.common.misc.TriState;
import me.lokka30.treasury.api.economy.account.AccountPermission;
import me.lokka30.treasury.api.economy.account.NonPlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.events.NonPlayerAccountTransactionEvent;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import me.lokka30.treasury.api.economy.transaction.EconomyTransaction;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redempt.redlib.commandmanager.Messages;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

public class TreasuryNpcAccount implements NonPlayerAccount {
	MultiCurrency instance;
	String accountname;
	String identifier;
	TreasuryManager treasuryManager;
	DatabaseManager dbm;
	private final EventBus eventBus = EventBus.INSTANCE;

	public TreasuryNpcAccount(MultiCurrency instance, String identifier, String accountname) {
		this.instance = instance;
		this.accountname = accountname;
		this.identifier = identifier;
		this.dbm = instance.getDBM();
		this.treasuryManager = instance.getTreasuryManager();
	}

	@Override
	public @NotNull String getIdentifier() {
		return this.identifier;
	}

	@Override
	public Optional<String> getName() {
		return Optional.ofNullable(this.accountname);
	}

	@Override
	public void setName(@Nullable String name, @NotNull EconomySubscriber<Boolean> subscription) {

	}

	@Override
	public void retrieveBalance(@NotNull Currency currency, @NotNull EconomySubscriber<BigDecimal> subscription) {
		BigDecimal value;
		DatabaseManager dbm = instance.getDBM();

		String currencyName = currency.getIdentifier();
		CurrencyType ctyp = treasuryManager.getCurrencyTypes().get(currencyName);

		if (!dbm.doesNonUserExists(identifier, ctyp)){
			dbm.createNonUser(identifier, ctyp);
		}

		value = dbm.getNonUserBalance(identifier, ctyp);

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
		DataFormatter dataFormatter;

		if (!treasuryManager.getTreasuryCurrency().containsKey(currencyName)) {
			subscription.fail(new EconomyException(FailureReasons.INVALID_CURRENCY));
		}
		else {
			ctyp = treasuryManager.getCurrencyTypes().get(currencyName);

			dataFormatter = new DataFormatter(ctyp);

			BigDecimal fixedAmount = dataFormatter.parseBigDecimal(amount);
			String formattedAmount = dataFormatter.formatBigDecimal(fixedAmount, true);

			boolean status = dbm.updateNonUserBalance(identifier, ctyp, fixedAmount);

			if (status) {
				EconomyTransactionInitiator.Type type = initiator.getType();
				String consolemsg;
				String initiatormsg;

				if (type == EconomyTransactionInitiator.Type.PLAYER) {
					UUID initiatorPlayerID = (UUID) initiator.getData();
					Player initiatorPlayer = (Player) Bukkit.getOfflinePlayer(initiatorPlayerID);
					consolemsg = "Player " + initiatorPlayer.getName() + " has set balance for " + identifier + "(NPC) to " + formattedAmount;

					initiatormsg = Messages.msg("prefix") + " Your have set balance for " + identifier + "(NPC) to " + formattedAmount;
					initiatorPlayer.sendMessage(initiatormsg);
				}
				else if (type == EconomyTransactionInitiator.Type.PLUGIN) {
					String pluginname = (String) initiator.getData();
					consolemsg = "Plugin " + pluginname + " has set balance for " + identifier + "(NPC) to " + formattedAmount;
				}
				else {
					consolemsg = "Server has set balance for " + identifier + "(NPC) to " + formattedAmount;
				}

				ConsoleLogger.info(consolemsg, ConsoleLogger.logTypes.log);

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

		DataFormatter dataFormatter;

		CurrencyType ctyp;

		if (!treasuryManager.getTreasuryCurrency().containsKey(currencyName)) {
			subscription.fail(new EconomyException(FailureReasons.INVALID_CURRENCY));
		}
		else {
			NonPlayerAccountTransactionEvent event = new NonPlayerAccountTransactionEvent(economyTransaction, this);
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

				String formattedAmount = dataFormatter.formatBigDecimal(fixedAmount, true);

				// check if the transaction is withdrawl or deposit
				BigDecimal previousAmount; // = BigDecimal.valueOf(0);
				BigDecimal newAmount; // = BigDecimal.valueOf(0);

				if (!dbm.doesNonUserExists(identifier, ctyp)) {
					dbm.createNonUser(identifier, ctyp);
				}

				previousAmount = dbm.getNonUserBalance(identifier, ctyp);
				boolean isDeposit = true;
				String transactionTypeFormatted;

				if (transactionType == EconomyTransactionType.WITHDRAWAL) {
					if (previousAmount.signum() <= 0){
						subscription.fail(new EconomyException(FailureReasons.BALANCE_NOT_ENOUGH));
					}
					else if (previousAmount.subtract(fixedAmount).signum() == -1) {
						subscription.fail(new EconomyException(FailureReasons.BALANCE_NOT_ENOUGH));
					}

					newAmount = previousAmount.subtract(fixedAmount);
					isDeposit = false;
					transactionTypeFormatted = "Withdrawal";
				}
				else {
					newAmount = previousAmount.add(fixedAmount);
					transactionTypeFormatted = "Deposit";
				}

				boolean status = dbm.updateNonUserBalance(identifier, ctyp, newAmount);

				if (status) {
					EconomyTransactionInitiator.Type type = initiator.getType();
					String consolemsg;
					String initiatormsg;
					String transactionFrom;

					if (type == EconomyTransactionInitiator.Type.PLAYER) {
						UUID initiatorPlayerID = (UUID) initiator.getData();
						Player initiatorPlayer = (Player) Bukkit.getOfflinePlayer(initiatorPlayerID);
						transactionFrom = initiatorPlayer.getName();
						consolemsg = "Player " + initiatorPlayer.getName() + " has given " + accountname + "(NPC) " + formattedAmount;

						if (isDeposit) {
							initiatormsg = Messages.msg("prefix") + " You have given " + initiatorPlayer.getName()+ "(NPC) " + formattedAmount;
							initiatorPlayer.sendMessage(initiatormsg);
						}
					}
					else if (type == EconomyTransactionInitiator.Type.PLUGIN) {
						String pluginname = (String) initiator.getData();
						transactionFrom = pluginname;
						consolemsg = "Plugin " + pluginname + " has given " + accountname + " " + formattedAmount;
					}
					else {
						transactionFrom = "Server";
						consolemsg = "Server has given " + accountname + " " + formattedAmount;
					}

					ConsoleLogger.info(consolemsg, ConsoleLogger.logTypes.log);

					if (ctyp.logTransactionEnabled()) {
						Table transactionsTable = dbm.getTables().get("Transactions");
						if (transactionsTable != null) {
							List<Column> temp = TransactionTable.TransactionData(timestamp, currencyName,
									newAmount.toString(), transactionTypeFormatted, transactionFrom,
									accountname, transactionReason);

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
			if (dbm.doesNonUserExists(identifier, x)) {
				if (instance.getTreasuryManager().getTreasuryCurrency().containsKey(x.getName())) {
					heldCurrencies.add(x.getName());
				}
				else {
					ConsoleLogger.info("Currency not loaded : " + x.getName(), ConsoleLogger.logTypes.debug);
				}
			}
			else {
				ConsoleLogger.info(accountname + "(NPC) not in table of currency : " + x.getName(), ConsoleLogger.logTypes.debug);
			}
		}

		subscription.succeed(heldCurrencies);
	}

	@Override
	public void retrieveTransactionHistory(int transactionCount, @NotNull Temporal from, @NotNull Temporal to, @NotNull EconomySubscriber<Collection<EconomyTransaction>> subscription) {
		// to be added (after i verify transaction are stored in db)
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
