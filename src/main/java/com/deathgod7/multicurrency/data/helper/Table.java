package com.deathgod7.multicurrency.data.helper;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.data.sqlite.SQLite;

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

    public static String getStringType(SQLite.DataType dataType) {
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

    public void insert(List<Column> columns) {
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
                
                PreparedStatement s = MultiCurrency.getInstance().getDbm().getConnection().prepareStatement(query.toString());
                for (int i = 0; i < columns.size(); i++) {
                    if (columns.get(i).dataType == SQLite.DataType.STRING) {
                        s.setString(i + 1, columns.get(i).getValue().toString());
                    } else if (columns.get(i).dataType == SQLite.DataType.INTEGER) {
                        s.setInt(i + 1, Integer.parseInt(columns.get(i).getValue().toString()));
                    } else {
                        s.setFloat(i + 1, Float.parseFloat(columns.get(i).getValue().toString()));
                    }
                }
                s.executeUpdate();
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("A row with that name already exists!");
        }
    }

    public List<Column> getExact(Column column) {
        List<Column> result = new ArrayList<>();
        String query = "SELECT * FROM " + getName() + " WHERE `" + column.getName() + "`=?";
        try {
            PreparedStatement s = MultiCurrency.getInstance().getDbm().getConnection().prepareStatement(query);
            if (column.dataType == SQLite.DataType.STRING) {
                s.setString(1, column.getValue().toString());
            } else if (column.dataType == SQLite.DataType.INTEGER) {
                s.setInt(1, Integer.parseInt(column.getValue().toString()));
            } else {
                s.setFloat(1, Float.parseFloat(column.getValue().toString()));
            }
            ResultSet rs = s.executeQuery();
            try {
                for (int i = 0; i < getColumns().size(); i++) {
                    Column rCol = new Column(getColumns().get(i).getName(), getColumns().get(i).dataType,
                            getColumns().get(i).limit);
                    if (rCol.dataType == SQLite.DataType.STRING) {
                        rCol.setValue(rs.getString(i + 1));
                    } else if (rCol.dataType == SQLite.DataType.INTEGER) {
                        rCol.setValue(rs.getInt(i + 1));
                    } else {
                        rCol.setValue(rs.getFloat(i + 1));
                    }
                    result.add(rCol);
                }
                MultiCurrency.getInstance().getDbm().close(s, rs);
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
                PreparedStatement s = MultiCurrency.getInstance().getDbm().getConnection().prepareStatement(query);
                if (column.dataType == SQLite.DataType.STRING) {
                    s.setString(1, column.getValue().toString());
                } else if (column.dataType == SQLite.DataType.INTEGER) {
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
                        if (getColumns().get(i).dataType == SQLite.DataType.STRING) {
                            rCol.setValue(rs.getString(i + 1));
                        } else if (getColumns().get(i).dataType == SQLite.DataType.INTEGER) {
                            rCol.setValue(rs.getInt(i + 1));
                        } else {
                            rCol.setValue(rs.getFloat(i + 1));
                        }
                        result.add(rCol);
                    }
                    results.add(result);
                }
                MultiCurrency.getInstance().getDbm().close(s, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return results;
        } else {
            return null;
        }
    }

    public List<List<Column>> getAll() {
        List<List<Column>> results = new ArrayList<>();
        String query = "SELECT * FROM " + getName();
        try {
            PreparedStatement s = MultiCurrency.getInstance().getDbm().getConnection().prepareStatement(query);
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                List<Column> result = new ArrayList<>();
                for (int i = 0; i < getColumns().size(); i++) {
                    Column rCol = new Column(getColumns().get(i).getName(), getColumns().get(i).dataType,
                            getColumns().get(i).limit);
                    if (getColumns().get(i).dataType == SQLite.DataType.STRING) {
                        s.setString(1, getColumns().get(i).getValue().toString());
                    } else if (getColumns().get(i).dataType == SQLite.DataType.INTEGER) {
                        s.setInt(1, Integer.parseInt(getColumns().get(i).getValue().toString()));
                    } else {
                        s.setFloat(1, Float.parseFloat(getColumns().get(i).getValue().toString()));
                    }
                    result.add(rCol);
                }
                results.add(result);
            }
            MultiCurrency.getInstance().getDbm().close(s, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void delete(Column column) {
        if (column.getName().equalsIgnoreCase(primaryKey.getName())) {
            String query = "DELETE FROM " + getName() + " WHERE `" + column.getName() + "`=?";
            try {
                PreparedStatement s = MultiCurrency.getInstance().getDbm().getConnection().prepareStatement(query);
                if (column.dataType == SQLite.DataType.STRING) {
                    s.setString(1, column.getValue().toString());
                } else if (column.dataType == SQLite.DataType.INTEGER) {
                    s.setInt(1, Integer.parseInt(column.getValue().toString()));
                } else {
                    s.setFloat(1, Float.parseFloat(column.getValue().toString()));
                }
                s.executeUpdate();
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Primary key must be used!");
        }
    }

    public void update(Column primaryKey, List<Column> columns) {
        if (!containsKey(columns)) {
            String query = "UPDATE " + getName() + " SET ";
            for (Column column : columns) {
                if (column.dataType == SQLite.DataType.STRING) {
                    query += "`" + column.getName() + "`='" + column.getValue().toString() + "'";
                } else {
                    query += "`" + column.getName() + "`=" + column.getValue().toString();
                }
                if (columns.indexOf(column) == columns.size() - 1) {
                    query += " ";
                } else {
                    query += ", ";
                }
            }
            query += "WHERE `" + primaryKey.getName() + "`=";
            if (primaryKey.dataType == SQLite.DataType.STRING) {
                query += "'" + primaryKey.getValue().toString() + "'";
            } else {
                query += primaryKey.getValue().toString();
            }
            try {
                PreparedStatement s = MultiCurrency.getInstance().getDbm().getConnection().prepareStatement(query);
                s.executeUpdate();
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            List<Column> newColumns = getExact(primaryKey);
            for (Column column : columns) {
                if (column.getName().equalsIgnoreCase(primaryKey.getName())) {
                    newColumns.set(0, column);
                } else {
                    newColumns.set(columns.indexOf(column), column);
                }
            }
            delete(primaryKey);
            insert(newColumns);
        }
    }

    private boolean containsKey(List<Column> columns) {
        for (Column column : columns) {
            if (column.getName().equalsIgnoreCase(primaryKey.getName())) {
                return true;
            }
        }
        return false;
    }

}
