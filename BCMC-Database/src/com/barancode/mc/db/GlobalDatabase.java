package com.barancode.mc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GlobalDatabase {
	Connection con;
	
	public void initialize(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC driver missing");
			e.printStackTrace();
			return;
		}
		
		try {
			con = DriverManager.getConnection("jdbc:mysql://192.99.45.178/bcmc", "bcmclocal", "3ENQG1WyWv_xNKu4sKueWk3_IIMFNi8xhMUm3fLmfu0E4zOC9mstx7HeSG_D7RSu");
		} catch (SQLException e) {
			System.out.println("MySQL connection failed!");
			e.printStackTrace();
			return;
		}
		
		if (con != null) {
			Statement statement = null;
			try {
				statement = con.createStatement();
				statement.execute("CREATE TABLE IF NOT EXISTS tokens(uuid VARCHAR(34), amount INT);");
				statement.execute("CREATE TABLE IF NOT EXISTS uuid(uuid VARCHAR(34), username VARCHAR(34));");
				statement.execute("CREATE TABLE IF NOT EXISTS apps(uuid VARCHAR(34), text TEXT, time BIGINT, votes INT);");
				statement.execute("CREATE TABLE IF NOT EXISTS commands(command TEXT, id VARCHAR(40));");
				statement.execute("CREATE TABLE IF NOT EXISTS colors(color INT, uuid VARCHAR(40));");
				statement.execute("CREATE TABLE IF NOT EXISTS time(uuid VARCHAR(34), time INT);");
				statement.execute("CREATE TABLE IF NOT EXISTS votes(uuid VARCHAR(34), amount INT);");
			} catch (SQLException e) {
				System.out.println("Initializing MySQL failed!");
				e.printStackTrace();
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}		 
			}
		} else {
			System.out.println("MySQL connection failed!");
		}
    }
	
	public void reconnect(){
		try {
			con = DriverManager.getConnection("jdbc:mysql://192.99.45.178/bcmc", "bcmclocal", "3ENQG1WyWv_xNKu4sKueWk3_IIMFNi8xhMUm3fLmfu0E4zOC9mstx7HeSG_D7RSu");
		} catch (SQLException e) {
			System.out.println("MySQL connection failed!");
			e.printStackTrace();
			return;
		}
	}
	
	public void quit(){
		try {
			if (con != null) con.close();
		} catch (SQLException e) {
			System.out.println("Could not close() the connection");
			e.printStackTrace();
		}
	}
}