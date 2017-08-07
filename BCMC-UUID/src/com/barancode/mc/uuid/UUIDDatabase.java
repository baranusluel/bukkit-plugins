package com.barancode.mc.uuid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import java.util.UUID;

public class UUIDDatabase {
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
			con = DriverManager.getConnection("jdbc:mysql://192.99.45.178/uuid", "bcmclocal", "3ENQG1WyWv_xNKu4sKueWk3_IIMFNi8xhMUm3fLmfu0E4zOC9mstx7HeSG_D7RSu");
		} catch (SQLException e) {
			System.out.println("MySQL connection failed!");
			e.printStackTrace();
			return;
		}
	 
		if (con != null) {
			Statement statement = null;
			try {
				statement = con.createStatement();
				statement.execute("CREATE TABLE IF NOT EXISTS uuid(uuid VARCHAR(34), username VARCHAR(34));");
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
	
	public void setPair(UUID uuid, String username){
		if (con != null) {
			Statement statement = null;
			ResultSet rs;
			try {
				statement = con.createStatement();
				rs = statement.executeQuery("SELECT * FROM uuid WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				if (rs.next()){
					if (rs.getString("username").equals(username)) return;
					else statement.execute("UPDATE uuid SET username=\"" + username + "\" WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				} else {
					statement.execute("INSERT INTO uuid(uuid, username) VALUES(\"" + uuid.toString().replaceAll("-", "") + "\", \"" + username + "\");");
				}
			} catch (SQLNonTransientConnectionException e){
				try {
					con = DriverManager.getConnection("jdbc:mysql://192.99.45.178/uuid", "bcmclocal", "3ENQG1WyWv_xNKu4sKueWk3_IIMFNi8xhMUm3fLmfu0E4zOC9mstx7HeSG_D7RSu");
					setPair(uuid, username);
				} catch (SQLException e1) {
					System.out.println("MySQL re-connection failed!");
					e1.printStackTrace();
				}
			} catch (SQLException e) {
				System.out.println("MySQL connection failed!");
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
			System.out.println("Attempting to re-initialize");
			initialize();
		}
    }
	
	public UUID getUUID(String username){
		String s = getValue(username, "username", "uuid").replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
		if (!s.equals("")){
			return UUID.fromString(s);
		} else {
			return null;
		}
	}
	
	public String getUsername(UUID uuid){
		return getValue(uuid.toString(), "uuid", "username");
	}
	
	public String getValue(String valueGiven, String typeGiven, String typeWanted){
		if (typeGiven.equals("uuid")) valueGiven = valueGiven.replaceAll("-", "");
		if (con != null) {
			Statement statement = null;
			ResultSet rs;
			try {
				statement = con.createStatement();
				rs = statement.executeQuery("SELECT * FROM uuid WHERE " + typeGiven + "=\"" + valueGiven + "\";");
				if (rs.next()){
					return rs.getString(typeWanted);
				}
				return "";
			} catch (SQLNonTransientConnectionException e){
				try {
					con = DriverManager.getConnection("jdbc:mysql://192.99.45.178/uuid", "bcmclocal", "3ENQG1WyWv_xNKu4sKueWk3_IIMFNi8xhMUm3fLmfu0E4zOC9mstx7HeSG_D7RSu");
					return getValue(valueGiven, typeGiven, typeWanted);
				} catch (SQLException e1) {
					System.out.println("MySQL re-connection failed!");
					e1.printStackTrace();
				}
			} catch (SQLException e) {
				System.out.println("MySQL connection failed!");
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
			System.out.println("Attempting to re-initialize");
			initialize();
		}
		return "";
    }
}
