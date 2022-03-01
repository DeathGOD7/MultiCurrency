package com.deathgod7.multicurrency.depends.economy;

import com.deathgod7.multicurrency.MultiCurrency;
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

public class Treasury implements Currency {
    CurrencyTypes currencyTypes;

    public Treasury(CurrencyTypes currencyTypes){
        this.currencyTypes = currencyTypes;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "Multi Currency - " + currencyTypes.getName();
    }

    @Override
    public @NotNull String getSymbol() {
        return currencyTypes.getCurrencySymbol();
    }

    @Override
    public char getDecimal() {
        return '.';
    }

    @Override
    public @NotNull String getDisplayNameSingular() {
        return currencyTypes.getCurrencySingularName();
    }

    @Override
    public @NotNull String getDisplayNamePlural() {
        return currencyTypes.getCurrencyPluralName();
    }

    @Override
    public int getPrecision() {
        return currencyTypes.getDecimalPrecision();
    }

    @Override
    public boolean isPrimary() {
        return Objects.equals(currencyTypes.getName(), MultiCurrency.getInstance().getMainConfig().primary_currency);
    }

    @Override
    public void to(@NotNull Currency currency, @NotNull BigDecimal amount, @NotNull EconomySubscriber<BigDecimal> subscription) {
        String currencyName = currency.getIdentifier().replace( "Multi Currency - ", "");
        Set<Currency> currencyCollection = MultiCurrency.getInstance().getTreasuryManager().getTreasuryHook().getCurrencies();

        if (currencyCollection == null) {
            return;
        }

        if (!currencyCollection.contains(currency)) {
            return;
        }

        Map<String, Double> conversionRates = currencyTypes.conversionRate;

        if (!conversionRates.containsKey(currencyName)){
            return;
        }

        Double rate = currencyTypes.conversionRate.get(currencyName);

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
        String displayFormat = currencyTypes.getDisplayFormat();
        String symbol = currencyTypes.getCurrencySymbol();
        String currency = currencyTypes.getName();
        char thousandSep = currencyTypes.getThousandSeperator();


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
        return c == currencyTypes.getThousandSeperator();
    }

    @Override
    public @NotNull BigDecimal getStartingBalance(@Nullable UUID playerID)
    {
        BigDecimal temp;
        if (playerID == null){
            temp = new BigDecimal(currencyTypes.startBal);
        }
        else{
            Player p = Bukkit.getPlayer(playerID.toString());
            assert p != null;
            temp = new BigDecimal(currencyTypes.getStartBal(p));
        }
        return  temp;
    }

//    this.displayFormat = "%balance% %currencysymbol%";

    @Override
    public @NotNull String format(@NotNull BigDecimal amount, @Nullable Locale locale) {
        String format = currencyTypes.getDisplayFormat();
        boolean isformatted = Boolean.parseBoolean(format.replace("%balance%", amount.toString()).replace("%currencysymbol%", currencyTypes.getCurrencySymbol()));

        if (isformatted) {
            return format;
        }
        else { return "ERROR_FORMATTING"; }
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount, @Nullable Locale locale, int precision) {
        String format = currencyTypes.getDisplayFormat();
        String amountformatted = amount.setScale(precision, RoundingMode.FLOOR).toString();
        boolean isformatted = Boolean.parseBoolean(format.replace("%balance%", amountformatted).replace("%currencysymbol%", currencyTypes.getCurrencySymbol()));

        if (isformatted) {
            return format;
        }
        else { return "ERROR_FORMATTING"; }
    }
}