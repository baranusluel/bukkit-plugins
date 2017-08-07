package com.barancode.contest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BCDatabase {
	private static String user;
	private static String password;
	private static String host;
	private static String database;
	
	public static boolean mysql = false;
	
	private Connection con;
	
	private static String getUser() {
		return user;
	}
	public static void setUser(String user) {
		BCDatabase.user = user;
	}
	private static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		BCDatabase.password = password;
	}
	private static String getHost() {
		return host;
	}
	public static void setHost(String host) {
		BCDatabase.host = host;
	}
	private static String getDatabase() {
		return database;
	}
	public static void setDatabase(String database) {
		BCDatabase.database = database;
	}
	
	public BCDatabase(){
		connect();
	}
	
	private boolean connect(){
		try {
			if (mysql){
				con = DriverManager.getConnection("jdbc:mysql://" + getHost() + "/" + getDatabase(), getUser(), getPassword());
			} else {
				con = DriverManager.getConnection("jdbc:sqlite:" + Main.instance.getDataFolder() + "/data.db");
			}
		} catch (SQLException e) {
			Main.log.warning("Unable to connect to SQL database!");
			e.printStackTrace();
		}
		return con != null;
	}
	
	private void disconnect(){
		try {
			if (con != null){
				con.close();
				con = null;
			}
		} catch (SQLException e){
			Main.log.warning("Unable to close the SQL connection!");
		}
	}
	
	public boolean reconnect(){
		disconnect();
		return connect();
	}
	
	public ResultSet query(String query)
	{
		return query(query, false);
	}
	
	private ResultSet query(String query, boolean retry)
	{
		if(con == null){
			Main.log.warning("Database connection was lost");
			if (!retry){
				if(!reconnect()) return null;
				Main.log.info("Reconnected to database, retrying query");
				return query(query, true);
			}
		}
		Statement statement = null;
		try {
			statement = con.createStatement();
		} catch (SQLException e) {
			return null;
		}
		
		ResultSet res = null;
		try {
			statement.execute(query);
			res = statement.getResultSet();
			return res;
		} catch (SQLException e) {
			Main.log.warning("Had a problem performing a database query");
			e.printStackTrace();
			if (!retry){
				if(!reconnect()) return null;
				Main.log.info("Reconnected to database, retrying query");
				return query(query, true);
			}
			return null;
		} finally {
			try { if (res != null) res.close(); } catch (Exception e) {};
			try { if (statement != null) statement.close(); } catch (Exception e) {};
		}
	}
}
