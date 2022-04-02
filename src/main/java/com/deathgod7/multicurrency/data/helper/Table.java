package com.deathgod7.multicurrency.data.helper;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.DatabaseManager;
import com.deathgod7.multicurrency.data.sqlite.SQLite;
import com.deathgod7.multicurrency.utils.ConsoleLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Table {

    private String name;
    private List<Column> columns = new ArrayList<>();
    private Column primaryKey;

    public Table(String name, Column primaryKey, Column... columns) {
        this.name = name;
        this.primaryKey = primaryKey;
        this.columns.addAll(Arrays.asList(columns));
    }

    public Table(String name, Collection<Column> columns, Column primaryKey) {
        this.name = name;
        this.primaryKey = primaryKey;
        this.columns.addAll(columns);
    }

    public Table(String name, Column... columns) {
        this.name = name;
        this.columns.addAll(Arrays.asList(columns));
        this.primaryKey = this.columns.get(0);
    }

    public Table(String name, Collection<Column> columns) {
        this.name = name;
        this.columns.addAll(columns);
        this.primaryKey = this.columns.get(0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public Column getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Column primaryKey) {
        this.primaryKey = primaryKey;
    }

    public static String getStringType(DatabaseManager.DataType dataType) {
        switch(dataType) {
            case STRING: return "VARCHAR";
            case INTEGER: return "INT";
            case FLOAT: return "FLOAT";
            default: return null;
        }
    }

    public String getDefaultQuery() {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS " + getName() + " (");
        for (Column column : getColumns()) {
            query.append("`").append(column.name).append("` ");
            query.append(Table.getStringType(column.dataType));
            query.append(column.limit > 0 ? " (" + column.limit + "), " : ", ");
        }
        query.append("PRIMARY KEY (`").append(primaryKey.getName()).append("`)");
        query.append(");");
        return query.toString();
    }

    public boolean insert(List<Column> columns) {
        if (getExact(columns.get(0)) == null) {
            StringBuilder query = new StringBuilder("INSERT INTO " + getName() + " (");
            for (Column column : columns) {
                if (columns.indexOf(column) < columns.size() - 1) {
                    query.append("`").append(column.getName()).append("`, ");
                } else {
                    query.append("`").append(column.getName()).append("`) ");
                }
            }
            query.append("VALUES (");
            for (int i = 0; i < columns.size(); i++) {
                if (i < columns.size() - 1) {
                    query.append("?, ");
                } else {
                    query.append("?)");
                }
            }
            query.append(";");

            try {
                PreparedStatement s = MultiCurrency.getInstance().getDBM().getConnection().prepareStatement(query.toString());
                for (int i = 0; i < columns.size(); i++) {
                    if (columns.get(i).dataType == DatabaseManager.DataType.STRING) {
                        s.setString(i + 1, columns.get(i).getValue().toString());
                    } else if (columns.get(i).dataType == DatabaseManager.DataType.INTEGER) {
                        s.setInt(i + 1, Integer.parseInt(columns.get(i).getValue().toString()));
                    } else {
                        s.setFloat(i + 1, Float.parseFloat(columns.get(i).getValue().toString()));
                    }
                }
                s.executeUpdate();
                s.close();
                return true;
            } catch (SQLException e) {
                ConsoleLogger.severe("Failed to insert into table " + getName() + " : " + e.getMessage(), ConsoleLogger.logTypes.debug);
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
    }

    public List<Column> getExact(Column column) {
        List<Column> result = new ArrayList<>();
        String query = "SELECT * FROM " + getName() + " WHERE `" + column.getName() + "`=?";
        try {
            PreparedStatement s = MultiCurrency.getInstance().getDBM().getConnection().prepareStatement(query);
            if (column.dataType == DatabaseManager.DataType.STRING) {
                s.setString(1, column.getValue().toString());
            } else if (column.dataType == DatabaseManager.DataType.INTEGER) {
                s.setInt(1, Integer.parseInt(column.getValue().toString()));
            } else {
                s.setFloat(1, Float.parseFloat(column.getValue().toString()));
            }
            ResultSet rs = s.executeQuery();
            try {
                for (int i = 0; i < getColumns().size(); i++) {
                    Column rCol = new Column(getColumns().get(i).getName(), getColumns().get(i).dataType,
                            getColumns().get(i).limit);
                    if (rCol.dataType == DatabaseManager.DataType.STRING) {
                        rCol.setValue(rs.getString(i + 1));
                    } else if (rCol.dataType == DatabaseManager.DataType.INTEGER) {
                        rCol.setValue(rs.getInt(i + 1));
                    } else {
                        rCol.setValue(rs.getFloat(i + 1));
                    }
                    result.add(rCol);
                }
                MultiCurrency.getInstance().getDBM().close(s, rs);
            } catch (SQLException e) {
                s.close();
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(result.size() > 0){
            return result;
        }
        else{
            return null;
        }
    }

    public List<List<Column>> search(Column column) {
        List<List<Column>> results = new ArrayList<>();
        if (!column.getName().equalsIgnoreCase(primaryKey.getName())) {
            String query = "SELECT * FROM " + getName() + " WHERE `" + column.getName() + "`=?";
            try {
                PreparedStatement s = MultiCurrency.getInstance().getDBM().getConnection().prepareStatement(query);
                if (column.dataType == DatabaseManager.DataType.STRING) {
                    s.setString(1, column.getValue().toString());
                } else if (column.dataType == DatabaseManager.DataType.INTEGER) {
                    s.setInt(1, Integer.parseInt(column.getValue().toString()));
                } else {
                    s.setFloat(1, Float.parseFloat(column.getValue().toString()));
                }
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    List<Column> result = new ArrayList<>();
                    for (int i = 0; i < getColumns().size(); i++) {
                        Column rCol = new Column(getColumns().get(i).getName(), getColumns().get(i).dataType,
                                getColumns().get(i).limit);
                        if (getColumns().get(i).dataType == DatabaseManager.DataType.STRING) {
                            rCol.setValue(rs.getString(i + 1));
                        } else if (getColumns().get(i).dataType == DatabaseManager.DataType.INTEGER) {
                            rCol.setValue(rs.getInt(i + 1));
                        } else {
                            rCol.setValue(rs.getFloat(i + 1));
                        }
                        result.add(rCol);
                    }
                    results.add(result);
                }
                MultiCurrency.getInstance().getDBM().close(s, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return results;
        } else {
            return null;
        }
    }

    public List<List<Column>> getAllColumns() {
        List<List<Column>> results = new ArrayList<>();
        String query = "SELECT * FROM " + getName();
        try {
            PreparedStatement s = MultiCurrency.getInstance().getDBM().getConnection().prepareStatement(query);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                List<Column> result = new ArrayList<>();
                for (int i = 0; i < getColumns().size(); i++) {
                    Column rCol = new Column(getColumns().get(i).getName(), getColumns().get(i).dataType,
                            getColumns().get(i).limit);
                    if (rCol.dataType == DatabaseManager.DataType.STRING) {
                        rCol.setValue(rs.getString(i + 1));
                    } else if (rCol.dataType == DatabaseManager.DataType.INTEGER) {
                        rCol.setValue(rs.getInt(i + 1));
                    } else {
                        rCol.setValue(rs.getFloat(i + 1));
                    }
                    result.add(rCol);
                }
                results.add(result);
            }
            MultiCurrency.getInstance().getDBM().close(s, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public boolean delete(Column column) {
        if (column.getName().equalsIgnoreCase(primaryKey.getName())) {
            String query = "DELETE FROM " + getName() + " WHERE `" + column.getName() + "`=?";
            try {
                PreparedStatement s = MultiCurrency.getInstance().getDBM().getConnection().prepareStatement(query);
                if (column.dataType == DatabaseManager.DataType.STRING) {
                    s.setString(1, column.getValue().toString());
                } else if (column.dataType == DatabaseManager.DataType.INTEGER) {
                    s.setInt(1, Integer.parseInt(column.getValue().toString()));
                } else {
                    s.setFloat(1, Float.parseFloat(column.getValue().toString()));
                }
                s.executeUpdate();
                s.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean update(Column primaryKey, List<Column> columns) {
        if (!containsPrimaryKey(columns)) {
            StringBuilder query = new StringBuilder("UPDATE " + getName() + " SET ");
            for (Column column : columns) {
                if (column.dataType == DatabaseManager.DataType.STRING) {
                    query.append("`").append(column.getName()).append("`='").append(column.getValue().toString()).append("'");
                } else {
                    query.append("`").append(column.getName()).append("`=").append(column.getValue().toString());
                }
                if (columns.indexOf(column) == columns.size() - 1) {
                    query.append(" ");
                } else {
                    query.append(", ");
                }
            }
            query.append("WHERE `").append(primaryKey.getName()).append("`=");
            if (primaryKey.dataType == DatabaseManager.DataType.STRING) {
                query.append("'").append(primaryKey.getValue().toString()).append("'");
            } else {
                query.append(primaryKey.getValue().toString());
            }
            try {
                PreparedStatement s = MultiCurrency.getInstance().getDBM().getConnection().prepareStatement(query.toString());
                s.executeUpdate();
                s.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        else {
            List<Column> newColumns = getExact(primaryKey);
            for (Column column : columns) {
                if (column.getName().equalsIgnoreCase(primaryKey.getName())) {
                    newColumns.set(0, column);
                } else {
                    newColumns.set(columns.indexOf(column), column);
                }
            }
            boolean deleted = delete(primaryKey);
            if (deleted){
                insert(newColumns);
                return true;
            }
            else {
                return false;
            }
        }
    }

    private boolean containsPrimaryKey(List<Column> columns) {
        for (Column column : columns) {
            if (column.getName().equalsIgnoreCase(primaryKey.getName())) {
                return true;
            }
        }
        return false;
    }

}
