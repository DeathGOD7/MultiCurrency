package com.github.deathgod7.multicurrency.data.mysql;

import com.github.deathgod7.multicurrency.MultiCurrency;

import java.sql.Connection;

public class MySQL {
	private MultiCurrency _multiCurrency;

	private String host;
	private String port;
	private String username;
	private String password;
	private String database;

	private Connection connection;
	public Connection getConnection(){
		if (connection == null){
			//connection = connectSQLite();
			return  connection;
		}
		return  connection;
	}

	//private HikariDataSource hikari;

	public MySQL(MultiCurrency multiCurrency){
		this._multiCurrency = multiCurrency;

		this.host = multiCurrency.getMainConfig().db_host;
		this.port = multiCurrency.getMainConfig().db_port;
		this.username = multiCurrency.getMainConfig().db_username;
		this.password = multiCurrency.getMainConfig().db_password;
		this.database = multiCurrency.getMainConfig().db_name;
	}

	public boolean isConnected(){
		return (connection != null);
	}

}
