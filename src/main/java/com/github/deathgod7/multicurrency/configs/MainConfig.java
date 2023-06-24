package com.github.deathgod7.multicurrency.configs;

import com.github.deathgod7.multicurrency.MultiCurrency;
import redempt.redlib.config.annotations.*;

@ConfigMappable
public class MainConfig {
	public transient String pluginversion = MultiCurrency.getPDFile().getVersion();

	@Comment("#############################################################################")
	@Comment("||                                                                         ||")
	@Comment("||                   Multi Currency - by Death GOD 7                       ||")
	@Comment("||                            - A solution to your custom needs            ||")
	@Comment("||                                                                         ||")
	@Comment("||               [ Github : https://github.com/DeathGOD7 ]                 ||")
	@Comment("||       [ Wiki : https://github.com/DeathGOD7/MultiCurrency/wiki ]        ||")
	@Comment("||                                                                         ||")
	@Comment("#############################################################################")
	@Comment("")
	@Comment("Some settings of the plugin which might come handy for your server and for debugging plugin.")
	@Comment("version = The plugin version you are using")
	@Comment("previousversion = The plugin version that you updated from")
	@Comment("debug = This allows you to get additional plugin information. Really really helpful for debugging")
	@Comment("disable_essentials = This will disable essentials economy vault hook")

	private String Settings;
	// Main Settings

	@ConfigName("Settings.version")
	public String version = pluginversion;
	@ConfigName("Settings.previousversion")
	public String previousversion = "";
	@ConfigName("Settings.debug")
	public boolean debug = false;
	@ConfigName("Settings.primary_currency")
	public String primary_currency = "Soul";
	@ConfigName("Settings.disable_essentials")
	public boolean disable_essentials = true;

	@Comment("Database support for this plugin either for cross server (bungeecord / veloctity or any proxy)")
	@Comment("or for save-storage / safe-keeping campaign. We go you covered fam!!")

	private String Database;
	// Database Settings

	@Comment("You can choose database type for either of these two type")
	@Comment("type = mysql, sqlite (Default : sqlite)")
	@ConfigName("Database.type")
	public String db_type = "sqlite"; //mysql and sqlite
	@Comment("NOTE : No need to touch any of this settings below if you are using sqlite database type in above settings")
	@Comment("Put your database host ip or custom domain address here")
	@ConfigName("Database.host")
	public String db_host = "database.example.com";
	@Comment("Put your database port number of the database address if any (MySQL Default Port : 3306)")
	@ConfigName("Database.port")
	public String db_port = "3306";
	@Comment("Put your username that you use to access the database")
	@ConfigName("Database.username")
	public String db_username = "root";
	@Comment("Put your password that you use to access the database. PS : It is top secret high security classified code right?")
	@ConfigName("Database.password")
	public String db_password = "toor";
	@ConfigName("Database.database")
	@Comment("Put the name of database that you want the data to be stored at")
	public String db_name = "multicurrency";



}







