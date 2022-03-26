package com.deathgod7.multicurrency.data;

import com.deathgod7.multicurrency.utils.TextUtils;
import org.bukkit.ChatColor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DataFormatter {

    public DecimalFormat decimalFormat;
    public BigDecimal maxNumber;
    public String displayformat;
    public String pluralname;
    public String singularname;
    public String symbol;
    public boolean isInt;

    public DataFormatter(String displayformat, String singularname, String pluralname, String maxBal, boolean isintegar, String thousandseperator, String symbol){
        this.displayformat = displayformat;
        this.singularname = singularname;
        this.pluralname = pluralname;
        this.maxNumber = getMaxNumber(maxBal);
        this.decimalFormat = new DecimalFormat();
        this.isInt = isintegar;
        this.symbol = symbol;

        if (!isInt) {
            decimalFormat.setMinimumFractionDigits(2);
            decimalFormat.setMaximumFractionDigits(2);
        }

        DecimalFormatSymbols spoint = new DecimalFormatSymbols();
        spoint.setGroupingSeparator(thousandseperator.toCharArray()[0]);
        decimalFormat.setDecimalFormatSymbols(spoint);

    }

    public BigDecimal parseString(String am) {
        BigDecimal bigDecimal = new BigDecimal(am);
        if (isInt) {
            return bigDecimal.setScale(0, RoundingMode.DOWN);
        } else {
            return bigDecimal.setScale(2, RoundingMode.DOWN);
        }
    }

    public BigDecimal parseDouble(double am) {
        BigDecimal bigDecimal = BigDecimal.valueOf(am);
        if (isInt) {
            return bigDecimal.setScale(0, RoundingMode.DOWN);
        } else {
            return bigDecimal.setScale(2, RoundingMode.DOWN);
        }
    }

    public BigDecimal parseBigDecimal(BigDecimal am) {
        if (isInt) {
            return am.setScale(0, RoundingMode.DOWN);
        } else {
            return am.setScale(2, RoundingMode.DOWN);
        }
    }

    public String formatBigDecimal(BigDecimal am, boolean withName) {
        if (withName) {
            if (am.compareTo(BigDecimal.ONE) == 0) {
                return decimalFormat.format(am) + singularname;
            }
            else {
                return decimalFormat.format(am) + pluralname;
            }
        }
        else {
            return TextUtils.ConvertTextColor('&', displayformat
                        .replace("%balance%", decimalFormat.format(am))
                        .replace("%currencysymbol%", symbol)
            );
        }
    }

    public String formatDouble(double am, boolean withName) {
        if (withName) {
            if (am > 1) {
                return decimalFormat.format(am) + pluralname;
            }
            else {
                return decimalFormat.format(am) + singularname;
            }
        }
        else {
            return TextUtils.ConvertTextColor('&', displayformat
                    .replace("%balance%", decimalFormat.format(am))
                    .replace("%currencysymbol%", symbol)
            );
        }
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