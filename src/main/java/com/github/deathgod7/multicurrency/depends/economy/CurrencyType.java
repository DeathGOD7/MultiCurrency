package com.github.deathgod7.multicurrency.depends.economy;

import com.github.deathgod7.multicurrency.configs.CurrencyConfig;
import redempt.redlib.config.annotations.*;

import java.util.HashMap;
import java.util.Map;

@ConfigMappable
@ConfigSubclassable
public class CurrencyType {
	@Comment("Note : Currency name shouldn't contain any spaces")
	@ConfigName("name")
	final String name;
	@ConfigName("symbol")
	final String currencysymbol;
	@ConfigName("min-balance")
	final String minBal;
	@ConfigName("max-balance")
	final String maxBal;
	@ConfigName("start-balance")
	final String startBal;
	@ConfigName("is-currency-int")
	final boolean isCurrencyInt;
	@ConfigName("log-transaction")
	final boolean logTransaction;
	@ConfigName("thousand-seperator")
	final String thousandSeperator;
	@ConfigName("singular-name")
	final String singularName;
	@ConfigName("plural-name")
	final String pluralName;
	@ConfigName("display-format")
	final String displayFormat;
	@ConfigName("decimal-precision")
	final int decimalPrecision;
	@ConfigName("conversion-rate")
	Map<String, Double> conversionRate = new HashMap<>();
	@ConfigName("custom-start-bal")
	Map<String, String> customStartBal = new HashMap<>();

	public CurrencyType(){
		this.name = "Soul";
		this.currencysymbol = "Soul";
		this.singularName = "Soul";
		this.pluralName = "Souls";
		this.displayFormat = "%balance% %currencysymbol%";
		this.decimalPrecision = 2;
		this.isCurrencyInt = true;
		this.logTransaction = false;
		this.thousandSeperator = ",";
		this.minBal = "0";
		this.maxBal = "10000000000000000";
		this.startBal = "0";
		this.conversionRate.put("AnotherCurrency", 1.0);
		this.conversionRate.put("Another2ndCurrency", 1.0);
		this.customStartBal.put("DeathGOD7", "69696969");
		this.customStartBal.put("DeathGOD7s_Milady", "70707070");
	}

	public CurrencyType(CurrencyConfig cfg){
		this.name = cfg.currency.name;
		this.currencysymbol = cfg.currency.currencysymbol;
		this.singularName = cfg.currency.singularName;
		this.pluralName = cfg.currency.pluralName;
		this.displayFormat = cfg.currency.displayFormat;
		this.minBal = cfg.currency.minBal;
		this.maxBal = cfg.currency.maxBal;
		this.startBal = cfg.currency.startBal;
		this.isCurrencyInt =  cfg.currency.isCurrencyInt;
		this.logTransaction = cfg.currency.logTransaction;
		this.thousandSeperator = cfg.currency.thousandSeperator;
		this.conversionRate = cfg.currency.conversionRate;
		this.customStartBal = cfg.currency.customStartBal;
		this.decimalPrecision = cfg.currency.decimalPrecision;
	}

	public CurrencyType(String name, String currencySymbol, String thousandseperator, String minbal, String maxbal, String startbal, int decimalPrecision, boolean currencyAsInt, boolean logTransaction, String singular, String plural, String displayformat, Map<String, Double> conversionRate, Map<String, String> customStartBal) {
		this.name = name;
		this.currencysymbol = currencySymbol;
		this.singularName = singular;
		this.pluralName = plural;
		this.displayFormat = displayformat;
		this.decimalPrecision = decimalPrecision;
		this.isCurrencyInt = currencyAsInt;
		this.thousandSeperator = thousandseperator;
		this.logTransaction = logTransaction;
		this.minBal = minbal;
		this.maxBal = maxbal;
		this.startBal = startbal;
		this.conversionRate = conversionRate;
		this.customStartBal = customStartBal;
	}


	public String getName(){
		return name;
	}

	public String getCurrencySymbol(){
		return currencysymbol;
	}

	public int getDecimalPrecision(){
		return decimalPrecision;
	}

	public Map<String, Double> getConversionRate(){
		return conversionRate;
	}

	public Map<String, String> getCustomStartBal(){
		return  customStartBal;
	}

	public String getMinBal(){
		return minBal;
	}

	public String getMaxBal(){
		return maxBal;
	}

	public String getStartBal(){
		return startBal;
	}

	public String getStartBal(String player){
		return customStartBal.getOrDefault(player, startBal);
	}

	public boolean isCurrencyInt(){
		return isCurrencyInt;
	}

	public boolean logTransactionEnabled() { return logTransaction; }

	public char getThousandSeperator(){
		return  thousandSeperator.toCharArray()[0];
	}

	public String getCurrencySingularName() { return singularName; }

	public String getCurrencyPluralName() { return pluralName; }

	public String getDisplayFormat() { return displayFormat; }

}
