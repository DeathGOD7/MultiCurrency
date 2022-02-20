package com.deathgod7.multicurrency.data.sqlite;

import com.deathgod7.multicurrency.MultiCurrency;
import com.deathgod7.multicurrency.utils.ConsoleLogger;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class SQLite {
    private final MultiCurrency _multiCurrency;

    private final String dbName;
    private boolean inDefaultFile = true;
    private String dirDB;
    private Connection connection;
    private File dbFile;

    public String getDbName(){
        return dbName;
    }

    public boolean isConnected(){
        return (connection != null);
    }

    public Connection getConnection(){
        if (connection == null){
            connection = connectSQLite();
        }
        return  connection;
    }

    public enum DataType {
        STRING,
        INTEGER,
        FLOAT
    }

    public SQLite(MultiCurrency multiCurrency, String dbName) {
        this._multiCurrency = multiCurrency;
        this.dbName = dbName;
        this.connection = connectSQLite();
    }

    public SQLite(MultiCurrency multiCurrency, String dbName, String directory) {
        this._multiCurrency = multiCurrency;
        this.dbName = dbName;
        this.inDefaultFile = false;
        this.dirDB = directory;
        this.connection = connectSQLite();
    }


    public Connection connectSQLite() {
        boolean isFolderCreated = false;
        boolean isFileCreated = false;
        if (inDefaultFile) {
            dbFile = new File(_multiCurrency.getDataFolder(), dbName + ".db");
        } else {
            isFolderCreated = new File(this.dirDB).mkdirs();
            if (isFolderCreated){
                ConsoleLogger.info("Database Folder Created : " + dirDB, ConsoleLogger.logTypes.debug);
            }
            else {
                ConsoleLogger.info("Database Folder Not Created or Already Exists : " + dirDB, ConsoleLogger.logTypes.debug);
            }
            dbFile = new File(this.dirDB + "/" + dbName + ".db");

        }

        ConsoleLogger.info("Database File : " + dbFile, ConsoleLogger.logTypes.debug);

        if (!dbFile.exists()) {
            try {
                isFileCreated = dbFile.createNewFile();
                if (isFileCreated){
                    ConsoleLogger.info("Database File Created : " + dbFile, ConsoleLogger.logTypes.debug);
                }
            } catch (IOException e) {
                ConsoleLogger.severe("Couldn't make the db file. Please check permisions and report back in github issue if it presist.", ConsoleLogger.logTypes.log);
            }
        }

        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            if (connection != null){
                ConsoleLogger.info("Connection to Database : SUCCESS", ConsoleLogger.logTypes.debug);
            }
            else{
                ConsoleLogger.info("Connection to Database : FAILED", ConsoleLogger.logTypes.debug);
            }
            return connection;
        } catch (SQLException ex) {
            ConsoleLogger.severe("SQLite exception on initialize. Please check permisions and report back in github issue if it presist.", ConsoleLogger.logTypes.log);
        } catch (ClassNotFoundException ex) {
            ConsoleLogger.severe("SQLite JBDC library class not found. Please check and report back in github issue if it presist.", ConsoleLogger.logTypes.log);
        }
        return null;
    }


    public void closeConnection() {
        if (isConnected()) {
            try {
                if (!(connection.isClosed())) {
                    connection.close();
                    connection = null;
                } else {
                    connection = null;
                }
            } catch (SQLException ex) {
                ConsoleLogger.severe(ex.getMessage(), ConsoleLogger.logTypes.debug);

            }
        }
    }
}
