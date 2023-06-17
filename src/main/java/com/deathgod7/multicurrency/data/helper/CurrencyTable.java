package com.deathgod7.multicurrency.data.helper;

import com.deathgod7.multicurrency.data.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class CurrencyTable {

	public static List<Column> CurrencyData() {
		List<Column> temp = new ArrayList<>();

		Column uuid = new Column("UUID", DatabaseManager.DataType.STRING, 100);
		Column playername = new Column("Name", DatabaseManager.DataType.STRING, 100);
		Column money = new Column("Money", DatabaseManager.DataType.STRING, 100);

		temp.add(uuid);
		temp.add(playername);
		temp.add(money);

		return temp;
	}

	public static List<Column> CurrencyData(String uuid, String name, String money) {
		List<Column> temp = new ArrayList<>();

		Column _uuid = new Column("UUID", uuid, DatabaseManager.DataType.STRING, 100);
		Column _name = new Column("Name", name, DatabaseManager.DataType.STRING, 100);
		Column _money = new Column("Money", money, DatabaseManager.DataType.STRING, 100);

		temp.add(_uuid);
		temp.add(_name);
		temp.add(_money);

		return temp;
	}

}
