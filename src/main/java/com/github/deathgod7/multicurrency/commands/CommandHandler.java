package com.github.deathgod7.multicurrency.commands;

import com.github.deathgod7.multicurrency.data.helper.Column;
import com.github.deathgod7.multicurrency.utils.TextUtils;
import com.github.deathgod7.multicurrency.MultiCurrency;
import com.github.deathgod7.multicurrency.data.DataFormatter;
import com.github.deathgod7.multicurrency.data.DatabaseManager;
import com.github.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.github.deathgod7.multicurrency.depends.economy.treasury.TreasuryAccountManager;
import com.github.deathgod7.multicurrency.depends.economy.treasury.TreasuryManager;
import com.github.deathgod7.multicurrency.utils.ConsoleLogger;
import com.github.deathgod7.multicurrency.utils.SE7ENUtils;
import me.lokka30.treasury.api.economy.account.NonPlayerAccount;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import me.lokka30.treasury.api.economy.transaction.EconomyTransaction;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionImportance;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionInitiator;
import me.lokka30.treasury.api.economy.transaction.EconomyTransactionType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.github.deathgod7.multicurrency.utils.TextUtils.ConvertTextColor;

public class CommandHandler {

	private final MultiCurrency instance;
	private final DatabaseManager dbm;
	private final TreasuryManager treasuryManager;
	private final TreasuryAccountManager tAM;
	private final String pluginPrefix;

	public CommandHandler(MultiCurrency instance) {
		this.instance = instance;
		this.dbm = instance.getDBM();
		this.treasuryManager = instance.getTreasuryManager();
		this.tAM = instance.getTreasuryAccountmanager();
		this.pluginPrefix = Messages.msg("prefix");
	}

	// -------------------------------------------------------------------
	// --------------------[ ECONOMY SECTION ]----------------------------
	// -------------------------------------------------------------------

	@CommandHook("ecolist")
	public void ecolist(CommandSender commandSender) {

		StringBuilder temp = new StringBuilder();
		for (String x : instance.getTreasuryManager().getTreasuryCurrency().keySet()) {
			temp.append(x).append(", ");
		}
		String ftemp = temp.substring(0, temp.length() - 2);
		commandSender.sendMessage("Available Currency : " + ftemp);


	}

	// -------------------------------------------------------------------
	// --------------------[ PLAYER SECTION ]-----------------------------
	// -------------------------------------------------------------------

	@CommandHook("info")
	public void info(CommandSender commandSender){
		String databaseType;
		String isConnected;
		String version = TextUtils.ConvertTextColor('&', "&ev"+MultiCurrency.getPDFile().getVersion());
		String apiversion = TextUtils.ConvertTextColor('&', "&e"+MultiCurrency.getPDFile().getAPIVersion());
		String developer = TextUtils.ConvertTextColor('&', "&1"+MultiCurrency.getPDFile().getAuthors());

		String temp1 = instance.getConfig().getString("Database.type");
		if (Objects.equals(temp1, "sqlite")){
			databaseType = TextUtils.ConvertTextColor('&', "&3SQLite");
		}
		else if (Objects.equals(temp1, "mysql")){
			databaseType = TextUtils.ConvertTextColor('&', "&3MySQL");
		}
		else{
			databaseType = TextUtils.ConvertTextColor('&', "&3SQLite");
		}

		if (dbm.isConnected()){
			isConnected = TextUtils.ConvertTextColor('&', "&2ONLINE");
		}
		else{
			isConnected = TextUtils.ConvertTextColor('&', "&4OFFLINE");
		}

		commandSender.sendMessage("Multi Currency");
		commandSender.sendMessage("Version : " + version);
		commandSender.sendMessage("Developer(s) : " + developer);
		commandSender.sendMessage("API Version : " + apiversion);
		commandSender.sendMessage("Database : " + databaseType);
		commandSender.sendMessage("Status : " + isConnected);
		//if


	}

	@CommandHook("reload")
	public void reload(CommandSender commandSender){
		String warning = "&aIt is best to restart the server as reloading will just break the plugin.";
//        String warning2 = "&aIf you want this feature then please request in discord with good reason.";
		commandSender.sendMessage(TextUtils.ConvertTextColor('&', warning));
//        commandSender.sendMessage(ConvertTextColor('&', warning2));
		instance.ReloadConfigs();
	}

	@CommandHook("debug")
	public void debug(CommandSender commandSender) {

		StringBuilder temp = new StringBuilder();
		for (String x : instance.getTreasuryManager().getTreasuryCurrency().keySet()) {
			temp.append(x).append(", ");
		}
		String ftemp = temp.substring(0, temp.length() - 2);
		commandSender.sendMessage("Available Currency : " + ftemp);

		StringBuilder temp2 = new StringBuilder();
		for (String x : instance.getDBM().getTables().keySet()) {
			temp2.append(x).append(", ");
		}
		String ftemp2 = temp2.substring(0, temp2.length() - 2);
		commandSender.sendMessage("Available Table : " + ftemp2);
	}

	@CommandHook("add")
	public void add(CommandSender commandSender, CurrencyType currencyType, OfflinePlayer target, int amount, boolean isSilent) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target.getName());
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
			commandSender.sendMessage("Currency Amount : " + amount);
			commandSender.sendMessage("Is Silent : " + isSilent);
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		DataFormatter dataFormatter = new DataFormatter(currencyType);

		if ( !tAM.hasPlayerAccount(target.getUniqueId() )) {
			tAM.registerPlayerAccount(target.getUniqueId());
		}

		PlayerAccount playerAccount = tAM.getPlayerAccount(target.getUniqueId());

		playerAccount.retrieveBalance(currency,
				new EconomySubscriber<BigDecimal>() {
					@Override
					public void succeed(@NotNull BigDecimal oldAmount) {
						BigDecimal newAmount = oldAmount.add(BigDecimal.valueOf(amount));
						String reason = "Adding balance to player account.";

						playerAccount.doTransaction(new EconomyTransaction(
										currency.getIdentifier(),
										new EconomyTransactionInitiator<Object>() {
											@Override
											public Object getData() {
												if (SE7ENUtils.isPlayer(commandSender)) {
													Player initiatorplayer = (Player) commandSender;
													return initiatorplayer.getUniqueId();
												}
												return null;
											}

											@Override
											public @NotNull Type getType() {
												if (SE7ENUtils.isPlayer(commandSender)) {
													return Type.PLAYER;
												} else {
													return Type.SERVER;
												}
											}
										},
										null,
										EconomyTransactionType.DEPOSIT,
										reason,
										new BigDecimal(amount),
										EconomyTransactionImportance.NORMAL
								),
								new EconomySubscriber<BigDecimal>() {
									@Override
									public void succeed(@NotNull BigDecimal bigDecimal) {
										commandSender.sendMessage(target.getName() + " balance is added by " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false));

										// if is silent, don't send message to player
										if (!isSilent) {
											if (target.isOnline() && target.getPlayer() != null) {  // If target is online
												target.getPlayer().sendMessage(TextUtils.ConvertTextColor("&aYour balance is added by " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false)));
											}
										}
										else {
											if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
												if (!SE7ENUtils.hasSilentPerms(commandSender)) {
													commandSender.sendMessage(TextUtils.ConvertTextColor(pluginPrefix + " &4Seems you don't have permission to send transaction silently." +
															"Receiver will now get transaction message."));
													if (target.isOnline() && target.getPlayer() != null) {
														target.getPlayer().sendMessage(TextUtils.ConvertTextColor("&aYour balance is added by " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false)));
													}
												}
											}
										}

									}

									@Override
									public void fail(@NotNull EconomyException exception) {
										commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't add money in player account!!"));
										ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
									}
								}
						);

						//debug
						ConsoleLogger.warn(target.getName() + " old balance is " + dataFormatter.formatBigDecimal(oldAmount, false), ConsoleLogger.logTypes.debug);
						ConsoleLogger.warn(target.getName() + " new balance is " + dataFormatter.formatBigDecimal(newAmount,false), ConsoleLogger.logTypes.debug);



					}

					@Override
					public void fail(@NotNull EconomyException exception) {
						commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't get player account from database!!"));
						ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
					}
				}
		);


	}

	@CommandHook("set")
	public void set(CommandSender commandSender, CurrencyType currencyType, OfflinePlayer target, int amount, boolean isSilent) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target.getName());
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
			commandSender.sendMessage("Currency Amount : " + amount);
			commandSender.sendMessage("Is Silent : " + isSilent);
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		DataFormatter dataFormatter = new DataFormatter(currencyType);

		if ( !tAM.hasPlayerAccount(target.getUniqueId() )) {
			tAM.registerPlayerAccount(target.getUniqueId());
		}

		PlayerAccount playerAccount = tAM.getPlayerAccount(target.getUniqueId());

		// update player balance using doTransaction after doing retrieveBalance
		playerAccount.retrieveBalance(currency,
				new EconomySubscriber<BigDecimal>() {
					@Override
					public void succeed(@NotNull BigDecimal oldAmount) {
						BigDecimal newAmount = BigDecimal.valueOf(amount);
						String reason = "Setting balance of player account.";
						EconomyTransactionType economyTransactionType;

						int comparisonResult = newAmount.compareTo(oldAmount);

						if (comparisonResult > 0) {
							economyTransactionType = EconomyTransactionType.DEPOSIT;
						}
						else if (comparisonResult < 0) {
							economyTransactionType = EconomyTransactionType.WITHDRAWAL;
						}
						else {
							commandSender.sendMessage(TextUtils.ConvertTextColor("&4Please don't try to set new amount of balance as previous one!!"));
							return;
						}

						playerAccount.doTransaction(new EconomyTransaction(
										currency.getIdentifier(),
										new EconomyTransactionInitiator<Object>() {
											@Override
											public Object getData() {
												if (SE7ENUtils.isPlayer(commandSender)) {
													Player initiatorplayer = (Player) commandSender;
													return initiatorplayer.getUniqueId();
												}
												return null;
											}

											@Override
											public @NotNull Type getType() {
												if (SE7ENUtils.isPlayer(commandSender)) {
													return Type.PLAYER;
												} else {
													return Type.SERVER;
												}
											}
										},
										null,
										economyTransactionType,
										reason,
										new BigDecimal(amount),
										EconomyTransactionImportance.NORMAL
								),
								new EconomySubscriber<BigDecimal>() {
									@Override
									public void succeed(@NotNull BigDecimal bigDecimal) {
										commandSender.sendMessage(target.getName() + " balance is set to " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false));

										// if is silent, don't send message to player
										if (!isSilent) {
											if (target.isOnline() && target.getPlayer() != null) {  // If target is online
												target.getPlayer().sendMessage(TextUtils.ConvertTextColor("&aYour balance is set to " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false)));
											}
										}
										else {
											if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
												if (!SE7ENUtils.hasSilentPerms(commandSender)) {
													commandSender.sendMessage(TextUtils.ConvertTextColor(pluginPrefix + " &4Seems you don't have permission to send transaction silently." +
															"Receiver will now get transaction message."));
													if (target.isOnline() && target.getPlayer() != null) {
														target.getPlayer().sendMessage(TextUtils.ConvertTextColor("&aYour balance is set to " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false)));
													}
												}
											}
										}

									}

									@Override
									public void fail(@NotNull EconomyException exception) {
										commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't set money in player account!!"));
										ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
									}
								}
						);

						//debug
						ConsoleLogger.warn(target.getName() + " old balance is " + dataFormatter.formatBigDecimal(oldAmount, false), ConsoleLogger.logTypes.debug);
						ConsoleLogger.warn(target.getName() + " new balance is " + dataFormatter.formatBigDecimal(newAmount,false), ConsoleLogger.logTypes.debug);



					}

					@Override
					public void fail(@NotNull EconomyException exception) {
						commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't get player account from database!!"));
						ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
					}
				}
		);



	}

	@CommandHook("take")
	public void take(CommandSender commandSender, CurrencyType currencyType, OfflinePlayer target, int amount, boolean isSilent) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target.getName());
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
			commandSender.sendMessage("Currency Amount : " + amount);
			commandSender.sendMessage("Is Silent : " + isSilent);
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		DataFormatter dataFormatter = new DataFormatter(currencyType);

		if ( !tAM.hasPlayerAccount(target.getUniqueId() )) {
			tAM.registerPlayerAccount(target.getUniqueId());
		}

		PlayerAccount playerAccount = tAM.getPlayerAccount(target.getUniqueId());

		// update player balance using doTransaction after doing retrieveBalance
		playerAccount.retrieveBalance(currency,
				new EconomySubscriber<BigDecimal>() {
					@Override
					public void succeed(@NotNull BigDecimal oldAmount) {
						BigDecimal newAmount = oldAmount.subtract(BigDecimal.valueOf(amount));
						String reason = "Taking balance from player account.";
						EconomyTransactionType economyTransactionType = EconomyTransactionType.WITHDRAWAL;

						playerAccount.doTransaction(new EconomyTransaction(
										currency.getIdentifier(),
										new EconomyTransactionInitiator<Object>() {
											@Override
											public Object getData() {
												if (SE7ENUtils.isPlayer(commandSender)) {
													Player initiatorplayer = (Player) commandSender;
													return initiatorplayer.getUniqueId();
												}
												return null;
											}

											@Override
											public @NotNull Type getType() {
												if (SE7ENUtils.isPlayer(commandSender)) {
													return Type.PLAYER;
												} else {
													return Type.SERVER;
												}
											}
										},
										null,
										economyTransactionType,
										reason,
										new BigDecimal(amount),
										EconomyTransactionImportance.NORMAL
								),
								new EconomySubscriber<BigDecimal>() {
									@Override
									public void succeed(@NotNull BigDecimal bigDecimal) {
										commandSender.sendMessage(target.getName() + " balance is deducted by " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false));

										// if is silent, don't send message to player
										if (!isSilent) {
											if (target.isOnline() && target.getPlayer() != null) {  // If target is online
												target.getPlayer().sendMessage(TextUtils.ConvertTextColor("&aYour balance is deducted by " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false)));
											}
										}
										else {
											if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
												if (!SE7ENUtils.hasSilentPerms(commandSender)) {
													commandSender.sendMessage(TextUtils.ConvertTextColor(pluginPrefix + " &4Seems you don't have permission to send transaction silently." +
															"Receiver will now get transaction message."));
													if (target.isOnline() && target.getPlayer() != null) {
														target.getPlayer().sendMessage(TextUtils.ConvertTextColor("&aYour balance is deducted by " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false)));
													}
												}
											}
										}

									}

									@Override
									public void fail(@NotNull EconomyException exception) {
										commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't deduct money in player account!!"));
										ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
									}
								}
						);

						//debug
						ConsoleLogger.warn(target.getName() + " old balance is " + dataFormatter.formatBigDecimal(oldAmount, false), ConsoleLogger.logTypes.debug);
						ConsoleLogger.warn(target.getName() + " new balance is " + dataFormatter.formatBigDecimal(newAmount,false), ConsoleLogger.logTypes.debug);



					}

					@Override
					public void fail(@NotNull EconomyException exception) {
						commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't get player account from database!!"));
						ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
					}
				}
		);


	}

	@CommandHook("give")
	public void give(CommandSender commandSender, CurrencyType currencyType, OfflinePlayer target, int amount, boolean isSilent) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {

			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target.getName());
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
			commandSender.sendMessage("Currency Amount : " + amount);
			commandSender.sendMessage("Is Silent : " + isSilent);
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor(pluginPrefix + " &4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor(pluginPrefix + " &4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		DataFormatter dataFormatter = new DataFormatter(currencyType);

		String initiatorName;
		String initiatorType = commandSender.getName();
		String reason;

		if ( !tAM.hasPlayerAccount(target.getUniqueId() )) {
			tAM.registerPlayerAccount(target.getUniqueId());
		}

		PlayerAccount receiverPlayerAccount = tAM.getPlayerAccount(target.getUniqueId());


		final boolean[] isWithdrawn = new boolean[1];
		final boolean[] isDeposited = new boolean[1];

		// check if command sender is player
		// if it is then deduct money from account
		if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
			Player initiator = (Player) commandSender;
			initiatorName = initiator.getName();    // get player name

			reason = initiator.getName() + " gave " + target.getName() + " " + amount + " of " + currencyType.getName() + " currency.";


			if ( !tAM.hasPlayerAccount(initiator.getUniqueId() )) {
				tAM.registerPlayerAccount(initiator.getUniqueId());
			}

			PlayerAccount initiatorPlayerAccount = tAM.getPlayerAccount(initiator.getUniqueId());

			initiatorPlayerAccount.doTransaction(new EconomyTransaction(
							currency.getIdentifier(),
							new EconomyTransactionInitiator<Object>() {
								@Override
								public Object getData() {
									return initiator.getUniqueId();
								}

								@Override
								public @NotNull Type getType() {
									return Type.PLAYER;
								}
							},
							null,
							EconomyTransactionType.WITHDRAWAL,
							reason,
							new BigDecimal(amount),
							EconomyTransactionImportance.NORMAL
					),
					new EconomySubscriber<BigDecimal>() {
						@Override
						public void succeed(@NotNull BigDecimal bigDecimal) {
							String initiatormsg = pluginPrefix + " You have given player " + target.getName() + " " + dataFormatter.formatBigDecimal(bigDecimal, false);
							String initiatormsg1 = pluginPrefix + " Your account has been deducted by " + dataFormatter.formatBigDecimal(bigDecimal, false);

							// send to initiator
							if (initiator.isOnline()) {
								initiator.sendMessage(TextUtils.ConvertTextColor(initiatormsg));
								initiator.sendMessage(TextUtils.ConvertTextColor(initiatormsg1));
							}

							isWithdrawn[0] = true;

						}

						@Override
						public void fail(@NotNull EconomyException exception) {
							String deductfail = pluginPrefix + " &4You transaction with " + target.getName() + " failed of " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false);
							commandSender.sendMessage(TextUtils.ConvertTextColor(deductfail));
							ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
							isWithdrawn[0] = false;
						}

					}
			);

			// check if withdraw is successfully done
			// else return and do nothing
			if (!isWithdrawn[0]) {
				return;
			}

		}
		else {
			initiatorName = commandSender.getName();
			reason = initiatorName + " gave " + target.getName() + " " + amount + " of " + currencyType.getName() + " currency.";
		}

		// then put to receiver
		receiverPlayerAccount.doTransaction(new EconomyTransaction(
						currency.getIdentifier(),
						new EconomyTransactionInitiator<Object>() {
							@Override
							public Object getData() {
								if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
									Player initiator = (Player) commandSender;
									return initiator.getUniqueId();
								}
								return null;
							}

							@Override
							public @NotNull Type getType() {
								if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
									return Type.PLAYER;
								}
								else {
									return Type.SERVER;
								}
							}
						},
						null,
						EconomyTransactionType.DEPOSIT,
						reason,
						new BigDecimal(amount),
						EconomyTransactionImportance.NORMAL
				),
				new EconomySubscriber<BigDecimal>() {
					@Override
					public void succeed(@NotNull BigDecimal bigDecimal) {
						String consolemsg = "&a" + initiatorName + " has given " + target.getName() + " " + dataFormatter.formatBigDecimal(bigDecimal, false) + " of " + currencyType.getName() + " currency.";

						String receivermsg = pluginPrefix + " " + initiatorName + " has given you " + dataFormatter.formatBigDecimal(bigDecimal, false);
						String receivermsg1 = pluginPrefix + " Your account has been credited by " + dataFormatter.formatBigDecimal(bigDecimal, false);

						// log in console
						ConsoleLogger.info(TextUtils.ConvertTextColor(consolemsg), ConsoleLogger.logTypes.log);

						// check if receiver is online
						// check if isSilent is true and check if sender has isSilent permission
						// if it does don't send message
						// if it doesn't send message regardless of isSilent

						if (target.isOnline()) {
							Player receiver = (Player) target;

							if (!isSilent) {
								receiver.sendMessage(TextUtils.ConvertTextColor(receivermsg));
								receiver.sendMessage(TextUtils.ConvertTextColor(receivermsg1));
							}
							else {
								if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
									if (!SE7ENUtils.hasSilentPerms(commandSender)) {
										commandSender.sendMessage(TextUtils.ConvertTextColor(pluginPrefix + " &4Seems you don't have permission to send transaction silently." +
												"Receiver will now get transaction message."));
										receiver.sendMessage(TextUtils.ConvertTextColor(receivermsg));
										receiver.sendMessage(TextUtils.ConvertTextColor(receivermsg1));
									}
								}
							}
						}

						// deposit success
						isDeposited[0] = true;

					}

					@Override
					public void fail(@NotNull EconomyException exception) {
						String addtoreceiverfail = pluginPrefix + " &4You transaction with " + target.getName() + " failed of " + dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false);
						commandSender.sendMessage(TextUtils.ConvertTextColor(addtoreceiverfail));

						ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);

						// deposit fail
						isDeposited[0] = false;
					}
				}
		);

		if (!isDeposited[0]) {
			// if receiver couldn't get the transaction, then put the money back to sender (only applies to player)
			if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
				Player initiator = (Player) commandSender;

				String failreason = initiator.getName() + " to " + target.getName() + " failed with " + amount + " of " + currencyType.getName() + " currency.";


				if ( !tAM.hasPlayerAccount(initiator.getUniqueId() )) {
					tAM.registerPlayerAccount(initiator.getUniqueId());
				}

				PlayerAccount initiatorPlayerAccount = tAM.getPlayerAccount(initiator.getUniqueId());

				initiatorPlayerAccount.doTransaction(new EconomyTransaction(
								currency.getIdentifier(),
								new EconomyTransactionInitiator<Object>() {
									@Override
									public Object getData() {
										return initiator.getUniqueId();
									}

									@Override
									public @NotNull Type getType() {
										return Type.PLAYER;
									}
								},
								null,
								EconomyTransactionType.DEPOSIT,
								failreason,
								new BigDecimal(amount),
								EconomyTransactionImportance.NORMAL
						),
						new EconomySubscriber<BigDecimal>() {
							@Override
							public void succeed(@NotNull BigDecimal bigDecimal) {
								String givebacksuccess = pluginPrefix + " &aYour account has been re-added by " + dataFormatter.formatBigDecimal(bigDecimal, false);
								ConsoleLogger.info(TextUtils.ConvertTextColor(givebacksuccess), ConsoleLogger.logTypes.log);

								// send to initiator
								if (initiator.isOnline()) {
									initiator.sendMessage(TextUtils.ConvertTextColor(givebacksuccess));
								}
							}

							@Override
							public void fail(@NotNull EconomyException exception) {
								String givebackfail = pluginPrefix + " &4Your failed transaction amount ("+ dataFormatter.formatBigDecimal(BigDecimal.valueOf(amount), false) +") couldn't be re-added to your account of " + currencyType.getName() + " currency." ;
								commandSender.sendMessage(TextUtils.ConvertTextColor(givebackfail));
								ConsoleLogger.severe(TextUtils.ConvertTextColor(givebackfail), ConsoleLogger.logTypes.log);
								ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
							}

						}
				);
			}
		}

	}

	@CommandHook("reset")
	public void reset(CommandSender commandSender, CurrencyType currencyType, OfflinePlayer target, boolean isSilent) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target.getName());
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
			commandSender.sendMessage("Is Silent : " + isSilent);
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		DataFormatter dataFormatter = new DataFormatter(currencyType);

		if ( !tAM.hasPlayerAccount(target.getUniqueId() )) {
			tAM.registerPlayerAccount(target.getUniqueId());
		}

		PlayerAccount playerAccount = tAM.getPlayerAccount(target.getUniqueId());

		playerAccount.setBalance(BigDecimal.ZERO, new EconomyTransactionInitiator<Object>() {
					@Override
					public Object getData() {
						if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
							Player initiatorplayer = (Player) commandSender;
							return initiatorplayer.getUniqueId();
						}
						return null;
					}

					@Override
					public @NotNull Type getType() {
						if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
							return Type.PLAYER;
						} else {
							return Type.SERVER;
						}
					}
				},
				currency,
				new EconomySubscriber<BigDecimal>() {
					@Override
					public void succeed(@NotNull BigDecimal bigDecimal) {
						commandSender.sendMessage(target.getName() + " balance is set to " + dataFormatter.formatBigDecimal(BigDecimal.ZERO, false));

						// if is silent, don't send message to player
						if (!isSilent) {
							if (target.isOnline() && target.getPlayer() != null) {  // If target is online
								target.getPlayer().sendMessage(TextUtils.ConvertTextColor("&aYour balance is set to " + dataFormatter.formatBigDecimal(BigDecimal.ZERO, false)));
							}
						}
						else {
							if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
								if (!SE7ENUtils.hasSilentPerms(commandSender)) {
									commandSender.sendMessage(TextUtils.ConvertTextColor(pluginPrefix + " &4Seems you don't have permission to send transaction silently." +
											"Receiver will now get transaction message."));
									if (target.isOnline() && target.getPlayer() != null) {
										target.getPlayer().sendMessage(TextUtils.ConvertTextColor("&aYour balance is set to " + dataFormatter.formatBigDecimal(BigDecimal.ZERO, false)));
									}
								}
							}
						}

					}

					@Override
					public void fail(@NotNull EconomyException exception) {
						commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't reset player money!!"));
						ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
					}
				}
		);



	}

	@CommandHook("bal")
	public void balself(CommandSender commandSender, CurrencyType currencyType) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
		}

		if (commandSender.getName().equalsIgnoreCase("CONSOLE")) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Cannot invoke this command from server console!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Please specify player if you want to view players balance."));
			return;
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		Player player = (Player) commandSender;
		DataFormatter dataFormatter = new DataFormatter(currencyType);

		if ( !tAM.hasPlayerAccount(player.getUniqueId() )) {
			tAM.registerPlayerAccount(player.getUniqueId());
		}

		PlayerAccount playerAccount = tAM.getPlayerAccount(player.getUniqueId());

		playerAccount.retrieveBalance(currency,
				new EconomySubscriber<BigDecimal>() {
					@Override
					public void succeed(@NotNull BigDecimal playerBalance) {
						commandSender.sendMessage("Your balance is " + dataFormatter.formatBigDecimal(playerBalance, false));
					}

					@Override
					public void fail(@NotNull EconomyException exception) {
						commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't get player account from database!!"));
						ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
					}
				}
		);


	}

	@CommandHook("balother")
	public void balother(CommandSender commandSender, CurrencyType currencyType, OfflinePlayer target) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target.getName());
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		DataFormatter dataFormatter = new DataFormatter(currencyType);

		if ( !tAM.hasPlayerAccount(target.getUniqueId() )) {
			tAM.registerPlayerAccount(target.getUniqueId());
		}

		PlayerAccount playerAccount = tAM.getPlayerAccount(target.getUniqueId());

		playerAccount.retrieveBalance(currency,
				new EconomySubscriber<BigDecimal>() {
					@Override
					public void succeed(@NotNull BigDecimal playerBalance) {
						commandSender.sendMessage(target.getName() + " balance is " + dataFormatter.formatBigDecimal(playerBalance, false));
					}

					@Override
					public void fail(@NotNull EconomyException exception) {
						commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't get player account from database!!"));
						ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
					}
				}
		);


	}

	@CommandHook("baltop")
	public void baltop(CommandSender commandSender, CurrencyType currencyType) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		List<List<Column>> temp = dbm.getOrderBalance(currencyType, DatabaseManager.OrderType.DESCENDING, DatabaseManager.AccountType.PLAYER);

		for (List<Column> data : temp) {
			String temmmp = "UUID : " + data.get(0).getValue().toString() + ", " +
					"Name : " + data.get(1).getValue().toString() + ", " +
					"Balance : " + data.get(2).getValue().toString();

			ConsoleLogger.warn(temmmp, ConsoleLogger.logTypes.debug);
		}

	}

	// -------------------------------------------------------------------
	// ------------------[ NON PLAYER SECTION ]---------------------------
	// -------------------------------------------------------------------

	// npc management section
	@CommandHook("npclist")
	public void npclist(CommandSender commandSender) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
		}

		HashMap<String, NonPlayerAccount> temp = tAM.getAllNpcAccounts();

		if (temp.isEmpty()) {
			commandSender.sendMessage("No NPC is created yet.");
			return;
		}

		StringBuilder list = new StringBuilder();
		int count = 0;

		for (NonPlayerAccount npcAcc : temp.values()) {
			count++;
			list.append(count).append(". ").append(npcAcc.getName()).append("\n");
		}

		list.delete(list.length()-2, list.length());

		commandSender.sendMessage(String.valueOf(list));

	}

	@CommandHook("npccreate")
	public void npccreate(CommandSender commandSender, String name) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("NPC Name : " + name);
		}

		if (!tAM.hasNpcAccount(tAM.npcIdPrefix + name)) {
			tAM.registerNpcAccount(tAM.npcIdPrefix + name, name);
		}
		else {
			commandSender.sendMessage("NPC account with name (" + name + ") already exists.");
		}

	}

	@CommandHook("npcdelete")
	public void npcdelete(CommandSender commandSender, String name) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("NPC Name : " + name);
		}

		if (tAM.hasNpcAccount(tAM.npcIdPrefix + name)) {
			boolean status = tAM.deleteNpcAccount(tAM.npcIdPrefix + name);
		}
		else {
			commandSender.sendMessage("NPC account with name (" + name + ") doesn't exists.");
		}
	}

	@CommandHook("npcrename")
	public void npcrename(CommandSender commandSender, String name) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("NPC Name : " + name);
		}

		if (tAM.hasNpcAccount(tAM.npcIdPrefix + name)) {
			boolean status = tAM.renameNpcAccount(tAM.npcIdPrefix + name);
		}
		else {
			commandSender.sendMessage("NPC account with name (" + name + ") doesn't exists.");
		}
	}

	// npc balance section
	@CommandHook("npcadd")
	public void npcadd(CommandSender commandSender, CurrencyType currencyType, String target, int amount) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target);
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
			commandSender.sendMessage("Currency Amount : " + amount);
		}

	}

	@CommandHook("npcset")
	public void npcset(CommandSender commandSender, CurrencyType currencyType, String target, int amount) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target);
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
			commandSender.sendMessage("Currency Amount : " + amount);
		}



	}

	@CommandHook("npctake")
	public void npctake(CommandSender commandSender, CurrencyType currencyType, String target, int amount) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target);
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
			commandSender.sendMessage("Currency Amount : " + amount);
		}



	}

	@CommandHook("npcgive")
	public void npcgive(CommandSender commandSender, CurrencyType currencyType, String target, int amount) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {

			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target);
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
			commandSender.sendMessage("Currency Amount : " + amount);
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		DataFormatter dataFormatter = new DataFormatter(currencyType);

		if ( !tAM.hasNpcAccount(target)) {
			String msg = TextUtils.ConvertTextColor("&4Couldn't get any account with " + target + " from database!!");
			if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
				commandSender.sendMessage(msg);
			}
			ConsoleLogger.severe(msg, ConsoleLogger.logTypes.log);
			return;
		}

		NonPlayerAccount nonPlayerAccount = tAM.getNpcAccount(target);

	}

	@CommandHook("npcbal")
	public void npcbal(CommandSender commandSender, CurrencyType currencyType, String target) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target);
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		DataFormatter dataFormatter = new DataFormatter(currencyType);
		final BigDecimal[] amount = {BigDecimal.ZERO};

		if ( !tAM.hasNpcAccount(target)) {
			String msg = TextUtils.ConvertTextColor("&4Couldn't get any account with " + target + " from database!!");
			if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
				commandSender.sendMessage(msg);
			}
			ConsoleLogger.severe(msg, ConsoleLogger.logTypes.log);
			return;
		}

		NonPlayerAccount nonPlayerAccount = tAM.getNpcAccount(target);

		nonPlayerAccount.retrieveBalance(currency,
				new EconomySubscriber<BigDecimal>() {
					@Override
					public void succeed(@NotNull BigDecimal bigDecimal) {
						amount[0] = bigDecimal;
						commandSender.sendMessage("NPC " + target + " balance is " + dataFormatter.formatBigDecimal(amount[0], false));
					}

					@Override
					public void fail(@NotNull EconomyException exception) {
						commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't get non player account from database!!"));
						ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
					}
				}
		);



	}

	@CommandHook("npcreset")
	public void npcreset(CommandSender commandSender, CurrencyType currencyType, String target) {
		if (instance.getMainConfig().debug && SE7ENUtils.hasDebugPerms(commandSender)) {
			commandSender.sendMessage("Command Sender : " + commandSender.getName());
			commandSender.sendMessage("Target : " + target);
			commandSender.sendMessage("Currency Type : " + currencyType.getName());
		}

		Currency currency = treasuryManager.getTreasuryCurrency().get(currencyType.getName());

		if (currency == null) {
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4Currency is not loaded properly in plugin!!"));
			commandSender.sendMessage(TextUtils.ConvertTextColor("&4If you think this is mistake, please open issues at github or contact on discord.!!"));
			return;
		}

		DataFormatter dataFormatter = new DataFormatter(currencyType);

		if ( !tAM.hasNpcAccount(target)) {
			String msg = TextUtils.ConvertTextColor("&4Couldn't get any account with " + target + " from database!!");
			if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
				commandSender.sendMessage(msg);
			}
			ConsoleLogger.severe(msg, ConsoleLogger.logTypes.log);
			return;
		}

		NonPlayerAccount nonPlayerAccount = tAM.getNpcAccount(target);

		nonPlayerAccount.setBalance(BigDecimal.ZERO, new EconomyTransactionInitiator<Object>() {
					@Override
					public Object getData() {
						if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
							Player initiatorplayer = (Player) commandSender;
							return initiatorplayer.getUniqueId();
						}
						return null;
					}

					@Override
					public @NotNull Type getType() {
						if (!commandSender.getName().equalsIgnoreCase("CONSOLE")) {
							return Type.PLAYER;
						} else {
							return Type.SERVER;
						}
					}
				},
				currency,
				new EconomySubscriber<BigDecimal>() {
					@Override
					public void succeed(@NotNull BigDecimal bigDecimal) {
						commandSender.sendMessage("NPC " + target + " balance is set to " + dataFormatter.formatBigDecimal(BigDecimal.ZERO, false));

					}

					@Override
					public void fail(@NotNull EconomyException exception) {
						commandSender.sendMessage(TextUtils.ConvertTextColor("&4Couldn't reset NPC " + target + " money!!"));
						ConsoleLogger.severe(exception.getMessage(), ConsoleLogger.logTypes.log);
					}
				}
		);



	}



}
