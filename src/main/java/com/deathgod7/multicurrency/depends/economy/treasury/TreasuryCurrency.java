package com.deathgod7.multicurrency.depends.economy.treasury;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.deathgod7.multicurrency.utils.TextUtils;
import me.lokka30.treasury.api.economy.currency.Currency;
import me.lokka30.treasury.api.economy.response.EconomyException;
import me.lokka30.treasury.api.economy.response.EconomySubscriber;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class TreasuryCurrency implements Currency {
	CurrencyType currencyType;

	public TreasuryCurrency(CurrencyType currencyType){
		this.currencyType = currencyType;
	}

	@Override
	public @NotNull String getIdentifier() {
		return currencyType.getName();
	}

	@Override
	public @NotNull String getSymbol() {
		return currencyType.getCurrencySymbol();
	}

	@Override
	public char getDecimal() {
		return '.';
	}

	@Override
	public @NotNull String getDisplayNameSingular() {
		return currencyType.getCurrencySingularName();
	}

	@Override
	public @NotNull String getDisplayNamePlural() {
		return currencyType.getCurrencyPluralName();
	}

	@Override
	public int getPrecision() {
		return currencyType.getDecimalPrecision();
	}

	@Override
	public boolean isPrimary() {
		return MultiCurrency.getInstance().getMainConfig().primary_currency.equals(currencyType.getName());
	}

	@Override
	public void to(@NotNull Currency currency, @NotNull BigDecimal amount, @NotNull EconomySubscriber<BigDecimal> subscription) {
		String currencyName = currency.getIdentifier();

		Set<Currency> currencyCollection = MultiCurrency.getInstance().getTreasuryManager().getTreasuryHook().getCurrencies();

		if (currencyCollection == null) {
			return;
		}

		if (!currencyCollection.contains(currency)) {
			return;
		}

		Map<String, Double> conversionRates = currencyType.getConversionRate();

		if (!conversionRates.containsKey(currencyName)){
			return;
		}

		Double rate = currencyType.getConversionRate().get(currencyName);

		BigDecimal convertedValue = amount.multiply(BigDecimal.valueOf(rate));

		if (convertedValue.intValueExact() <= 0){
			subscription.fail(new EconomyException(FailureReasons.INVALID_VALUE));
		}
		else {
			subscription.succeed(convertedValue);
		}
	}

	@Override
	public void parse(@NotNull String formatted, @NotNull EconomySubscriber<BigDecimal> subscription) {
		StringBuilder valueBuilder = new StringBuilder();
		String displayFormat = currencyType.getDisplayFormat();
		String symbol = currencyType.getCurrencySymbol();
		String currency = currencyType.getName();
		char thousandSep = currencyType.getThousandSeperator();


		String[] split = displayFormat.replace("%currencysymbol%", symbol).split("%balance%");
		for (String x : split) {
			displayFormat = displayFormat.replace(x, "");
		}

		boolean hadDot = false;

		for (char c : displayFormat.toCharArray()) {
			if (Character.isWhitespace(c)) {
				continue;
			}

			if (isSeparator(c)) {
				continue;
			}

			if (Character.isDigit(c)) {
				valueBuilder.append(c);
			}

			if (c == '.') {
				hadDot = true;
				valueBuilder.append(c);
			}
		}

		if (valueBuilder.length() == 0) {
			subscription.fail(new EconomyException(FailureReasons.INVALID_VALUE));
			return;
		}

		String stringvalue = valueBuilder.toString();

		try {
			BigDecimal value = new BigDecimal(stringvalue);
			if (value.intValueExact() < 0) {
				subscription.fail(new EconomyException(FailureReasons.NEGATIVE_BALANCES_NOT_SUPPORTED));
				return;
			}

			subscription.succeed(value);

		} catch (NumberFormatException e) {
			subscription.fail(new EconomyException(FailureReasons.INVALID_VALUE, e));
		}
	}

	private boolean isSeparator(char c) {
		return c == currencyType.getThousandSeperator();
	}

	@Override
	public @NotNull BigDecimal getStartingBalance(@Nullable UUID playerID)
	{
		BigDecimal temp;
		if (playerID == null){
			temp = new BigDecimal(currencyType.getStartBal());
		}
		else{
			Player p = Bukkit.getPlayer(playerID);
			assert p != null;
			temp = new BigDecimal(currencyType.getStartBal(p.getName()));
		}
		return  temp;
	}

//    this.displayFormat = "%balance% %currencysymbol%";

	@Override
	public @NotNull String format(@NotNull BigDecimal amount, @Nullable Locale locale) {
		String format = currencyType.getDisplayFormat();
		boolean isformatted = Boolean.parseBoolean(format.replace("%balance%", amount.toString()).replace("%currencysymbol%", currencyType.getCurrencySymbol()));

		if (isformatted) {
			return format;
		}
		else { return "ERROR_FORMATTING"; }
	}

	@Override
	public @NotNull String format(@NotNull BigDecimal amount, @Nullable Locale locale, int precision) {
		String format = currencyType.getDisplayFormat();
		String amountformatted = amount.setScale(precision, RoundingMode.FLOOR).toString();
		boolean isformatted = Boolean.parseBoolean(format.replace("%balance%", amountformatted).replace("%currencysymbol%", currencyType.getCurrencySymbol()));

		if (isformatted) {
			return format;
		}
		else { return "ERROR_FORMATTING"; }
	}
}
