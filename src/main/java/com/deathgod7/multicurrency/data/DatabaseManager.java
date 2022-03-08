package com.deathgod7.multicurrency.data;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.helper.Column;
import com.deathgod7.multicurrency.data.mysql.MySQL;
import com.deathgod7.multicurrency.data.sqlite.SQLite;
import com.deathgod7.multicurrency.data.helper.Table;
import com.deathgod7.multicurrency.depends.economy.CurrencyTypes;
import com.deathgod7.multicurrency.utils.ConsoleLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import sun.tools.jconsole.Tab;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager {
    private final MultiCurrency _multiCurrency;
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

    public DatabaseManager(MultiCurrency multiCurrency){
        _multiCurrency = multiCurrency;
        choosenDB = _multiCurrency.getMainConfig().db_type;
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
            _multiCurrency.getMainConfig().db_type = "sqlite";
            return _sqlite.getConnection();
        }
    }

    public String getDBName() {
        if (Objects.equals(choosenDB, "sqlite")) {
            return  _sqlite.getDbName();
        }
        else if (Objects.equals(choosenDB, "mysql")){
            // to do mysql support
            return _multiCurrency.getMainConfig().db_name;
        }
        else {
            _multiCurrency.getMainConfig().db_type = "sqlite";
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
            _sqlite = new SQLite(_multiCurrency, "database", _multiCurrency.getPluginFolder().resolve("Database").toString());
            ConsoleLogger.info("Database Type : SQLite", ConsoleLogger.logTypes.log);

        }
        else if (Objects.equals(choosenDB, "mysql")){
            // to do mysql support
            ConsoleLogger.info("Database Type : MySQL", ConsoleLogger.logTypes.log);
        }
        else {
            _multiCurrency.getMainConfig().db_type = "sqlite";
            _sqlite = new SQLite(_multiCurrency, "database");
            ConsoleLogger.info("Database Type : SQLite", ConsoleLogger.logTypes.log);
        }
    }

    public void loadMysqlTables() {
        // SELECT table_name FROM information_schema.tables;
        String query = "SELECT * FROM information_schema.columns " +
                "WHERE table_schema = '"+ this.getDBName() +"' " +
                "ORDER BY table_name,ordinal_position";
        try {
            PreparedStatement ps = this.getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ConsoleLogger.info(rs.toString(), ConsoleLogger.logTypes.log);
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadSqliteTable() {
        // SELECT * FROM sqlite_master;
        String queryx = "SELECT * FROM sqlite_master " +
                "WHERE table_schema = '"+ this.getDBName() +"' " +
                "ORDER BY table_name,ordinal_position";
        String query = "SELECT * FROM sqlite_master WHERE type = 'table'";
        try {
            PreparedStatement ps = this.getConnection().prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ConsoleLogger.info("Response : " + rs , ConsoleLogger.logTypes.debug);
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Table> getTables() {
        return tables;
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

        ConsoleLogger.info("Query : " + query, ConsoleLogger.logTypes.debug);


        try {
            PreparedStatement ps = MultiCurrency.getInstance().getDbm().getConnection().prepareStatement(query.toString());
            ps.executeUpdate();
            ps.close();
            tables.put(table.getName(), table);
        } catch (SQLException ex) {
            ConsoleLogger.severe("Couldn't make the required table. Please check permissions and report back in github issue if it presist.", ConsoleLogger.logTypes.log);
            ex.printStackTrace();
        }
   }

   public boolean createUser(Player player, CurrencyTypes ctyp){
        Table table = tables.get(ctyp.getName());
        List<Column> temp = new ArrayList<>();

        if (!doesUserExists(player, ctyp)) {
            Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
            Column playername = new Column("Player", player.getName(), SQLite.DataType.STRING, 100);
            Column money = new Column("Money", ctyp.getStartBal(), SQLite.DataType.STRING, 100);

            temp.add(uuid);
            temp.add(playername);
            temp.add(money);

            table.insert(temp);

            return true;
        }
       return false;
   }

    public boolean createUser(UUID playerID, CurrencyTypes ctyp){
        Table table = tables.get(ctyp.getName());
        List<Column> temp = new ArrayList<>();
        Player player = Bukkit.getPlayer(playerID);

        if (player == null) {
            return  false;
        }

        if (!doesUserExists(playerID, ctyp)) {
            Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
            Column playername = new Column("Player", player.getName(), SQLite.DataType.STRING, 100);
            Column money = new Column("Money", ctyp.getStartBal(), SQLite.DataType.STRING, 100);

            temp.add(uuid);
            temp.add(playername);
            temp.add(money);

            table.insert(temp);

            return true;
        }
        return false;
    }

   public boolean doesUserExists(Player player, CurrencyTypes ctyp){
       Table table = tables.get(ctyp.getName());
       Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
       return table.getExact(uuid) != null;
   }

    public boolean doesUserExists(UUID playerID, CurrencyTypes ctyp){
        Table table = tables.get(ctyp.getName());

        Player player = Bukkit.getPlayer(playerID);

        if (player == null) {
            return  false;
        }

        Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
        return table.getExact(uuid) != null;
    }

    public boolean updateBalance(Player player, CurrencyTypes ctyp, BigDecimal newmoney){
        Table table = tables.get(ctyp.getName());

        Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
        Column money = new Column("Money", newmoney.toString(), SQLite.DataType.STRING, 100);

        List<Column> temp = new ArrayList<>();
        temp.add(money);

        return table.update(uuid, temp);
    }

    public boolean updateBalance(UUID playerID, CurrencyTypes ctyp, BigDecimal newmoney){
        Table table = tables.get(ctyp.getName());

        Player player = Bukkit.getPlayer(playerID);

        if (player == null) {
            return  false;
        }

        Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
        Column money = new Column("Money", newmoney.toString(), SQLite.DataType.STRING, 100);

        List<Column> temp = new ArrayList<>();
        temp.add(money);

        return table.update(uuid, temp);
    }

    public boolean gettBalance(Player player, CurrencyTypes ctyp){
        Table table = tables.get(ctyp.getName());

        Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
        Column money = new Column("Money", "0", SQLite.DataType.STRING, 100);

        List<Column> temp = new ArrayList<>();
        temp.add(money);

        return table.update(uuid, temp);
    }

    public boolean getBalance(UUID playerID, CurrencyTypes ctyp){
        Table table = tables.get(ctyp.getName());

        Player player = Bukkit.getPlayer(playerID);

        if (player == null) {
            return  false;
        }

        Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
        Column money = new Column("Money", "0", SQLite.DataType.STRING, 100);

        List<Column> temp = new ArrayList<>();
        temp.add(money);

        return table.update(uuid, temp);
    }

    public boolean resetBalance(Player player, CurrencyTypes ctyp){
        Table table = tables.get(ctyp.getName());

        Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
        Column money = new Column("Money", "0", SQLite.DataType.STRING, 100);

        List<Column> temp = new ArrayList<>();
        temp.add(money);

        return table.update(uuid, temp);
    }

    public boolean resetBalance(UUID playerID, CurrencyTypes ctyp){
        Table table = tables.get(ctyp.getName());

        Player player = Bukkit.getPlayer(playerID);

        if (player == null) {
            return  false;
        }

        Column uuid = new Column("UUID", player.getUniqueId().toString(), SQLite.DataType.STRING, 100);
        Column money = new Column("Money", "0", SQLite.DataType.STRING, 100);

        List<Column> temp = new ArrayList<>();
        temp.add(money);

        return table.update(uuid, temp);
    }

}
