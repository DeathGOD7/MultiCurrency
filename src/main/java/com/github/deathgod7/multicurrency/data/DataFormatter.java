package com.github.deathgod7.multicurrency.data;

import com.github.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.github.deathgod7.multicurrency.utils.TextUtils;

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

	public DataFormatter(CurrencyType ctype){

		this.displayformat = ctype.getDisplayFormat();
		this.singularname = ctype.getCurrencySingularName();
		this.pluralname = ctype.getCurrencyPluralName();
		this.maxNumber = getMaxNumber(ctype.getMaxBal());
		this.decimalFormat = new DecimalFormat();
		this.isInt = ctype.isCurrencyInt();
		this.symbol = ctype.getCurrencySymbol();

		if (!isInt) {
			decimalFormat.setMinimumFractionDigits(2);
		} else {
			decimalFormat.setMaximumFractionDigits(0);
		}

		DecimalFormatSymbols spoint = new DecimalFormatSymbols();
		spoint.setGroupingSeparator(ctype.getThousandSeperator());
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

	public String formatBigDecimal(BigDecimal am, boolean withCurrencyName) {
		if (withCurrencyName) {
			if (am.compareTo(BigDecimal.ONE) == 0) {
				return decimalFormat.format(am) + " " + singularname.toLowerCase();
			}
			else {
				return decimalFormat.format(am) + " " + pluralname.toLowerCase();
			}
		}
		else {
			return TextUtils.ConvertTextColor('&', displayformat
					.replace("%balance%", decimalFormat.format(am))
					.replace("%currencysymbol%", symbol)
			);
		}
	}

	public String formatDouble(double am, boolean withCurrencyName) {
		if (withCurrencyName) {
			if (am > 1) {
				return decimalFormat.format(am) + pluralname.toLowerCase();
			}
			else {
				return decimalFormat.format(am) + singularname.toLowerCase();
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