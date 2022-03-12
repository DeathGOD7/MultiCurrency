package com.deathgod7.multicurrency.data;

import org.bukkit.ChatColor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DataFormatter {

    public DecimalFormat decimalFormat;
    public BigDecimal maxNumber;
    String displayformat; // = XConomy.config.getString("Currency.display-format");
    String pluralname; // = XConomy.config.getString("Currency.plural-name");
    String singularname;// = XConomy.config.getString("Currency.singular-name");
    boolean isInt;

    public DataFormatter(String displayformat, String singularname, String pluralname, String maxBal, boolean isintegar, String thousandseperator){
        this.displayformat = displayformat;
        this.singularname = singularname;
        this.pluralname = pluralname;
        this.maxNumber = getMaxNumber(maxBal);
        this.decimalFormat = new DecimalFormat();
        this.isInt = isintegar;

        if (!isInt) {
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMaximumFractionDigits(2);
        }

        DecimalFormatSymbols spoint = new DecimalFormatSymbols();
        spoint.setGroupingSeparator(thousandseperator.toCharArray()[0]);
        decimalFormat.setDecimalFormatSymbols(spoint);


        //ServerINFO.PaymentTax = setpaymenttax();
    }

    public BigDecimal formatString(String am) {
        BigDecimal bigDecimal = new BigDecimal(am);
        if (isInt) {
            return bigDecimal.setScale(0, RoundingMode.DOWN);
        } else {
            return bigDecimal.setScale(2, RoundingMode.DOWN);
        }
    }

    public BigDecimal formatdouble(double am) {
        BigDecimal bigDecimal = BigDecimal.valueOf(am);
        if (isInt) {
            return bigDecimal.setScale(0, RoundingMode.DOWN);
        } else {
            return bigDecimal.setScale(2, RoundingMode.DOWN);
        }
    }

    public BigDecimal formatdouble(BigDecimal am) {
        if (isInt) {
            return am.setScale(0, RoundingMode.DOWN);
        } else {
            return am.setScale(2, RoundingMode.DOWN);
        }
    }

    //@SuppressWarnings("ConstantConditions")
    public String formatBigDecimal(BigDecimal am) {
        if (am.compareTo(BigDecimal.ONE) == 0) {
            return ChatColor.translateAlternateColorCodes('&', displayformat
                    .replace("%balance%", decimalFormat.format(am))
                    .replace("%currencyname%", singularname));
        }
        return ChatColor.translateAlternateColorCodes('&', displayformat
                .replace("%balance%", decimalFormat.format(am))
                .replace("%currencyname%", pluralname));
    }

    public String formatDouble(double am) {
        if (am > 1) {
            return ChatColor.translateAlternateColorCodes('&', displayformat
                    .replace("%balance%", decimalFormat.format(am))
                    .replace("%currencyname%", pluralname));
        }
        return ChatColor.translateAlternateColorCodes('&', displayformat
                .replace("%balance%", decimalFormat.format(am))
                .replace("%currencyname%", singularname));
    }

    public boolean isMAX(BigDecimal am) {
        return am.compareTo(maxNumber) > 0;
    }


    public BigDecimal getMaxNumber(String maxn) {
        BigDecimal defaultmaxnumber = new BigDecimal("10000000000000000");
        if (maxn == null) {
            return defaultmaxnumber;
        }
        if (maxn.length() > 17) {
            return defaultmaxnumber;
        }
        BigDecimal mnumber = new BigDecimal(maxn);
        if (mnumber.compareTo(defaultmaxnumber) >= 0) {
            return defaultmaxnumber;
        } else {
            return mnumber;
        }
    }
}