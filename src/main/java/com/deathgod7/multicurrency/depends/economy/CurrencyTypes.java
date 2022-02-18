package com.deathgod7.multicurrency.depends.economy;

import com.deathgod7.multicurrency.data.DataFormatter;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.config.annotations.ConfigMappable;
import redempt.redlib.config.annotations.ConfigSubclassable;

@ConfigMappable
@ConfigSubclassable
public class CurrencyTypes {
    final String name;
    final String currencysymbol;
    final String minBal;
    final String maxBal;
    final String startBal;
    final boolean isCurrencyInt;
    final String thousandSeperator;

    final String singularName;
    final String pluralrName;
    final String displayFormat;
    //this._displayFormat = "%balance% %currencyname%";
    //Long.parseLong(s

    final transient DataFormatter dataFormatter;

    public CurrencyTypes(){
        this.name = "Soul Currency";
        this.currencysymbol = "Soul";
        this.singularName = "Soul";
        this.pluralrName = "Souls";
        this.displayFormat = "%balance% %currencysymbol%";
        this.isCurrencyInt = true;
        this.thousandSeperator = ",";
        dataFormatter = new DataFormatter(displayFormat, singularName, pluralrName, null, isCurrencyInt, thousandSeperator);
        this.minBal = "0";
        this.maxBal = dataFormatter.maxNumber.toString();
        this.startBal = "0";
    }

    public CurrencyTypes(ConfigManager cfg){
        this.name = cfg.getConfig().getString("name");
        this.currencysymbol = cfg.getConfig().getString("currencysymbol");
        this.singularName = cfg.getConfig().getString("singularName");
        this.pluralrName = cfg.getConfig().getString("pluralrName");
        this.displayFormat = cfg.getConfig().getString("displayFormat");
        this.minBal = cfg.getConfig().getString("minBal");
        this.maxBal = cfg.getConfig().getString("maxBal");
        this.startBal = cfg.getConfig().getString("startBal");
        this.isCurrencyInt =  cfg.getConfig().getBoolean("startBal");
        this.thousandSeperator = cfg.getConfig().getString("thousandSeperator");
        dataFormatter = new DataFormatter(displayFormat, singularName, pluralrName, null, isCurrencyInt, thousandSeperator);
    }

    public CurrencyTypes(String name, String currencysymbol, String minbal, String maxbal, String startbal, boolean currencyAsInt, String singular, String plural, String baldisplayformat) {
        this.name = name;
        this.currencysymbol = currencysymbol;
        this.singularName = singular;
        this.pluralrName = plural;
        this.displayFormat = baldisplayformat;
        this.isCurrencyInt = currencyAsInt;
        this.thousandSeperator = ",";
        dataFormatter = new DataFormatter(displayFormat, singularName, pluralrName, null, isCurrencyInt, thousandSeperator);
        this.minBal = minbal;
        this.maxBal = dataFormatter.maxNumber.toString();
        this.startBal = startbal;
    }


    public String getName(){
        return name;
    }

    public String getCurrencySymbol(){
        return currencysymbol;
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

    public boolean getCurrencyFractional(){
        return isCurrencyInt;
    }

    public String getThousandSeperator(){
        return  thousandSeperator;
    }



}
