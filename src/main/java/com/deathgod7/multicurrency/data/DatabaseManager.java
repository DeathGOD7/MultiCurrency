package com.deathgod7.multicurrency.data;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.helper.*;
import com.deathgod7.multicurrency.data.mysql.MySQL;
import com.deathgod7.multicurrency.data.sqlite.SQLite;
import com.deathgod7.multicurrency.depends.economy.CurrencyType;
import com.deathgod7.multicurrency.depends.economy.treasury.TreasuryAccountManager;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import me.lokka30.treasury.api.economy.account.NonPlayerAccount;
import me.lokka30.treasury.api.economy.account.PlayerAccount;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager {
	private final MultiCurrency instance;

	private final TreasuryAccountManager tAM;
	private SQLite _sqlite;
	private MySQL _mysql;

	public SQLite getSQLite() {
		return _sqlite;
	}

	public MySQL getMySQL() {
		return _mysql;
	}

	public boolean isConnected(){
		return (getConnection() != null);
	}

	private final Map<String, Table> tables = new HashMap<>();

	String choosenDB;

	public enum DataType {
		STRING,
		INTEGER,
		FLOAT
	}

	public enum OrderType {
		ASCENDING,
		DESCENDING
	}

	public enum AccountType {
		PLAYER,
		NPC
	}

	public DatabaseManager(MultiCurrency multiCurrency){
		instance = multiCurrency;
		tAM = instance.getTreasuryAccountmanager();
		choosenDB = instance.getMainConfig().db_type;
		loadDB();
	}

	public Connection getConnection(){
		if (Objects.equals(choosenDB, "sqlite")) {
			return  _sqlite.getConnection();
		}
		else if (Objects.equals(choosenDB, "mysql")){
			// to do mysql support
			return null;
		}
		else {
			instance.getMainConfig().db_type = "sqlite";
			return _sqlite.getConnection();
		}
	}

	public String getDBName() {
		if (Objects.equals(choosenDB, "sqlite")) {
			return  _sqlite.getDbName();
		}
		else if (Objects.equals(choosenDB, "mysql")){
			// to do mysql support
			return instance.getMainConfig().db_name;
		}
		else {
			instance.getMainConfig().db_type = "sqlite";
			return  _sqlite.getDbName();
		}
	}

	public void close(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			ConsoleLogger.severe(ex.getMessage(), ConsoleLogger.logTypes.debug);
		}
	}

	public void loadDB(){
		if (Objects.equals(choosenDB, "sqlite")){
			_sqlite = new SQLite(instance, "database", instance.getPluginFolder().resolve("Database").toString());
			ConsoleLogger.info("Database Type : SQLite", ConsoleLogger.logTypes.log);
			loadSqliteTable();
			ConsoleLogger.info("Loaded database!", ConsoleLogger.logTypes.log);
		}
		else if (Objects.equals(choosenDB, "mysql")){
			// to do mysql support
			ConsoleLogger.info("Database Type : MySQL", ConsoleLogger.logTypes.log);
			loadMysqlTables();
			ConsoleLogger.info("Loaded database!", ConsoleLogger.logTypes.log);
		}
		else {
			instance.getMainConfig().db_type = "sqlite";
			_sqlite = new SQLite(instance, "database");
			ConsoleLogger.info("Database Type : SQLite", ConsoleLogger.logTypes.log);
			loadSqliteTable();
			ConsoleLogger.info("Loaded database!", ConsoleLogger.logTypes.log);
		}
	}

	public void loadMysqlTables() {
		String query = "SELECT table_name FROM information_schema.tables " +
				"WHERE table_schema = '"+ this.getDBName() +"' AND table_type = 'base table' " +
				"ORDER BY table_name";
		try {
			PreparedStatement ps = this.getConnection().prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String tablename = rs.getString(1);
				ConsoleLogger.warn("Loaded Table : " + tablename, ConsoleLogger.logTypes.debug);
				tables.put(tablename, new Table(tablename, CurrencyTable.CurrencyData()));
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadSqliteTable() {
		String query = "SELECT tbl_name FROM sqlite_schema " +
				"WHERE type = 'table' AND name NOT LIKE 'sqlite_%' " +
				"ORDER by tbl_name";
		try {
			PreparedStatement ps = this.getConnection().prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String tablename = rs.getString(1);
				ConsoleLogger.warn("Loaded Table : " + tablename, ConsoleLogger.logTypes.debug);
				tables.put(tablename, new Table(tablename, CurrencyTable.CurrencyData()));
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Map<String, Table> getTables() {
		return tables;
	}

	public void createAccountTable() {
		Table table = new Table("TreasuryAccounts", AccountTable.AccountData());
		createTable(table);
	}

	public void createTransactionTable() {
		Table table = new Table("Transactions", TransactionTable.TransactionData());
		createTable(table);
	}

	public void createTable(Table table) {
		StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + table.getName() + "` (");
		for (Column column : table.getColumns()) {
			query.append("`").append(column.name).append("` ");
			query.append(Table.getStringType(column.dataType));
			query.append(column.limit > 0 ? " (" + column.limit + "), " : ", ");
		}
		query.append("PRIMARY KEY (`").append(table.getPrimaryKey().getName()).append("`)");
		query.append(");");

		ConsoleLogger.warn("Query from Create Table : " + query, ConsoleLogger.logTypes.debug);


		try {
			PreparedStatement ps = MultiCurrency.getInstance().getDBM().getConnection().prepareStatement(query.toString());
			ps.executeUpdate();
			ps.close();
			tables.put(table.getName(), table);
		} catch (SQLException ex) {
			ConsoleLogger.severe("Couldn't make the required table. Please check permissions and report back in github issue if it presist.", ConsoleLogger.logTypes.log);
			ex.printStackTrace();
		}
	}

	public boolean createUser(Player player, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());

		if (!doesUserExists(player, ctyp)) {
			List<Column> temp = CurrencyTable.CurrencyData(player.getUniqueId().toString(),
					player.getName(),
					ctyp.getStartBal(player.getName())
			);
			return table.insert(temp);
		}
		ConsoleLogger.info("User ("+ player.getName() +") account already exists in " + ctyp.getName() + " currency. Skipping account creation.", ConsoleLogger.logTypes.debug);
		return false;
	}

	public boolean createUser(UUID uuid, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());

		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

		if (!doesUserExists(uuid, ctyp)) {
			List<Column> temp = CurrencyTable.CurrencyData(uuid.toString(),
					player.getName(),
					ctyp.getStartBal(player.getName())
			);
			return table.insert(temp);
		}
		ConsoleLogger.info("User ("+ player.getName() +") account already exists in " + ctyp.getName() + " currency. Skipping account creation.", ConsoleLogger.logTypes.debug);
		return false;
	}

	public boolean createNonUser(String identifier, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());

		if (!doesNonUserExists(identifier, ctyp)) {
			List<Column> temp = CurrencyTable.CurrencyData(identifier,
					identifier,
					ctyp.getStartBal()
			);

			return table.insert(temp);
		}
		ConsoleLogger.info("Non User ("+ identifier +") account already exists in " + ctyp.getName() + " currency. Skipping account creation.", ConsoleLogger.logTypes.debug);
		return false;
	}

	public boolean doesUserExists(Player player, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());
		Column uuid = new Column("UUID", player.getUniqueId().toString(), DatabaseManager.DataType.STRING, 100);
		boolean status =  !table.getExact(uuid).isEmpty();
		ConsoleLogger.warn("[USERCHECK] User Exists in " + ctyp.getName() + "? " + String.valueOf(status).toUpperCase(), ConsoleLogger.logTypes.debug);
		return status;
	}

	public boolean doesUserExists(UUID playerID, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());
		Column uuid = new Column("UUID", playerID.toString(), DatabaseManager.DataType.STRING, 100);
		boolean status =  !table.getExact(uuid).isEmpty();
		ConsoleLogger.warn("[USERCHECK] User Exists in " + ctyp.getName() + "? " + String.valueOf(status).toUpperCase(), ConsoleLogger.logTypes.debug);
		return status;
	}

	public boolean doesNonUserExists(String identifier, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());
		Column uuid = new Column("UUID", identifier, DatabaseManager.DataType.STRING, 100);
		boolean status =  !table.getExact(uuid).isEmpty();
		ConsoleLogger.warn("Non User Exists in " + ctyp.getName() + "? " + String.valueOf(status).toUpperCase(), ConsoleLogger.logTypes.debug);
		return status;
	}

	public boolean updateBalance(Player player, CurrencyType ctyp, BigDecimal newmoney){
		Table table = tables.get(ctyp.getName());

		if (!doesUserExists(player, ctyp)){
			createUser(player, ctyp);
		}

		Column uuid = new Column("UUID", player.getUniqueId().toString(), DatabaseManager.DataType.STRING, 100);
		Column money = new Column("Money", newmoney.toString(), DatabaseManager.DataType.STRING, 100);

		List<Column> temp = new ArrayList<>();
		temp.add(money);

		return table.update(uuid, temp);
	}

	public boolean updateBalance(UUID playerID, CurrencyType ctyp, BigDecimal newmoney){
		Table table = tables.get(ctyp.getName());

		if (!doesUserExists(playerID, ctyp)){
			createUser(playerID, ctyp);
		}

		Column uuid = new Column("UUID", playerID.toString(), DatabaseManager.DataType.STRING, 100);
		Column money = new Column("Money", newmoney.toString(), DatabaseManager.DataType.STRING, 100);

		List<Column> temp = new ArrayList<>();
		temp.add(money);

		return table.update(uuid, temp);
	}

	public boolean updateNonUserBalance(String identifier, CurrencyType ctyp, BigDecimal newmoney){
		Table table = tables.get(ctyp.getName());

		if (!doesNonUserExists(identifier, ctyp)) {
			createNonUser(identifier, ctyp);
		}

		Column uuid = new Column("UUID", identifier, DatabaseManager.DataType.STRING, 100);
		Column money = new Column("Money", newmoney.toString(), DatabaseManager.DataType.STRING, 100);

		List<Column> temp = new ArrayList<>();
		temp.add(money);

		return table.update(uuid, temp);
	}

	public BigDecimal getBalance(Player player, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());

		if (!doesUserExists(player, ctyp)){
			createUser(player, ctyp);
			return BigDecimal.valueOf(0);
		}

		Column uuid = new Column("UUID", player.getUniqueId().toString(), DatabaseManager.DataType.STRING, 100);

		HashMap<String, Column> tableslist =  new HashMap<>();
		for (Column c : table.getExact(uuid)) {
			tableslist.put(c.getName(), c);
		}

		String playermoney = tableslist.get("Money").getValue().toString();

		return new BigDecimal(playermoney);
	}

	public BigDecimal getBalance(UUID playerID, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());

		OfflinePlayer player = Bukkit.getOfflinePlayer(playerID);

		if (!doesUserExists(playerID, ctyp)){
			createUser(playerID, ctyp);
			return BigDecimal.valueOf(0);
		}

		Column uuid = new Column("UUID", playerID.toString(), DatabaseManager.DataType.STRING, 100);

		HashMap<String, Column> tableslist =  new HashMap<>();
		for (Column c : table.getExact(uuid)) {
			tableslist.put(c.getName(), c);
		}

		String playermoney = tableslist.get("Money").getValue().toString();

		return new BigDecimal(playermoney);
	}

	public BigDecimal getNonUserBalance(String identifier, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());

		if (!doesNonUserExists(identifier, ctyp)) {
			createNonUser(identifier, ctyp);
			return BigDecimal.valueOf(0);
		}

		Column uuid = new Column("UUID", identifier, DatabaseManager.DataType.STRING, 100);

		HashMap<String, Column> tableslist =  new HashMap<>();
		for (Column c : table.getExact(uuid)) {
			tableslist.put(c.getName(), c);
		}

		String playermoney = tableslist.get("Money").getValue().toString();

		return new BigDecimal(playermoney);
	}

	public boolean resetBalance(Player player, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());

		Column uuid = new Column("UUID", player.getUniqueId().toString(), DatabaseManager.DataType.STRING, 100);
		Column money = new Column("Money", "0", DatabaseManager.DataType.STRING, 100);

		List<Column> temp = new ArrayList<>();
		temp.add(money);

		return table.update(uuid, temp);
	}

	public boolean resetBalance(UUID playerID, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());

		OfflinePlayer player = Bukkit.getOfflinePlayer(playerID);

		Column uuid = new Column("UUID", playerID.toString(), DatabaseManager.DataType.STRING, 100);
		Column money = new Column("Money", "0", DatabaseManager.DataType.STRING, 100);

		List<Column> temp = new ArrayList<>();
		temp.add(money);

		return table.update(uuid, temp);
	}

	public boolean resetNonUserBalance(String identifier, CurrencyType ctyp){
		Table table = tables.get(ctyp.getName());

		Column uuid = new Column("UUID", identifier, DatabaseManager.DataType.STRING, 100);
		Column money = new Column("Money", "0", DatabaseManager.DataType.STRING, 100);

		List<Column> temp = new ArrayList<>();
		temp.add(money);

		return table.update(uuid, temp);
	}

	public List<List<Column>> getOrderBalance(CurrencyType ctyp, OrderType orderType, AccountType accountType) {
		// player data
		Table currencytTable = tables.get(ctyp.getName());
		List<List<Column>> allCurrencyData = currencytTable.getAllColumns();
		List<List<Column>> filteredCurrencyData = new ArrayList<>();

		// account data
		HashMap<String, PlayerAccount> playerAccountsTable = tAM.getAllPlayerAccounts();
		HashMap<String, NonPlayerAccount> npcAccountsTable = tAM.getAllNpcAccounts();

		// remove all Player/NPC account from currency table
		for (List<Column> data : allCurrencyData) {
			String value = data.get(0).getValue().toString();

			if ((accountType == AccountType.PLAYER && !npcAccountsTable.containsKey(value))
					|| (accountType == AccountType.NPC && !playerAccountsTable.containsKey(value))) {
				filteredCurrencyData.add(data);
			}
		}

		// Create a custom comparator
		Comparator<List<Column>> customComparator = new Comparator<List<Column>>() {
			@Override
			public int compare(List<Column> list1, List<Column> list2) {
				// Compare based on a specific column value or any other criteria
				// For example, if the column index is 1 and contains integers:
				int value1 = Integer.parseInt(list1.get(2).getValue().toString());
				int value2 = Integer.parseInt(list2.get(2).getValue().toString());

				// Determine the sort order based on a parameter
				// Set to true for ascending order, false for descending order
				boolean isAscendingOrder = orderType.equals(OrderType.ASCENDING);
				int sortOrder = isAscendingOrder ? 1 : -1;

				// Compare based on the sort order
				return Integer.compare(value1, value2) * sortOrder;
			}
		};

		// Sort the list based on the specified sort order
		filteredCurrencyData.sort(customComparator);

		return filteredCurrencyData;
	}


}
