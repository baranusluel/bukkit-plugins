package com.barancode.mc.tokens;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import java.util.UUID;

public class TokenDatabase {
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
			con = DriverManager.getConnection("jdbc:mysql://192.99.45.178/tokens", "bcmclocal", "3ENQG1WyWv_xNKu4sKueWk3_IIMFNi8xhMUm3fLmfu0E4zOC9mstx7HeSG_D7RSu");
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
	
	public void setAmount(UUID uuid, int amount){
		if (con != null) {
			Statement statement = null;
			ResultSet rs;
			try {
				statement = con.createStatement();
				rs = statement.executeQuery("SELECT * FROM tokens WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				if (rs.next()){
					statement.execute("UPDATE tokens SET amount=" + amount + " WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				} else {
					statement.execute("INSERT INTO tokens(uuid, amount) VALUES(\"" + uuid.toString().replaceAll("-", "") + "\", " + amount + ");");
				}
			} catch (SQLNonTransientConnectionException e){
				try {
					con = DriverManager.getConnection("jdbc:mysql://192.99.45.178/tokens", "bcmclocal", "3ENQG1WyWv_xNKu4sKueWk3_IIMFNi8xhMUm3fLmfu0E4zOC9mstx7HeSG_D7RSu");
					setAmount(uuid, amount);
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
			try {
				con = DriverManager.getConnection("jdbc:mysql://192.99.45.178/tokens", "bcmclocal", "3ENQG1WyWv_xNKu4sKueWk3_IIMFNi8xhMUm3fLmfu0E4zOC9mstx7HeSG_D7RSu");
				setAmount(uuid, amount);
			} catch (SQLException e1) {
				System.out.println("MySQL re-connection failed!");
				e1.printStackTrace();
			}
		}
    }
	
	public int getAmount(UUID uuid){
		return getAmount(uuid.toString().replaceAll("-", ""));
	}
	
	private int getAmount(String uuid){
		if (con != null) {
			Statement statement = null;
			ResultSet rs;
			try {
				statement = con.createStatement();
				rs = statement.executeQuery("SELECT * FROM tokens WHERE uuid=\"" + uuid + "\";");
				if (rs.next()){
					return rs.getInt("amount");
				}
				return 0;
			} catch (SQLNonTransientConnectionException e){
				try {
					con = DriverManager.getConnection("jdbc:mysql://192.99.45.178/tokens", "bcmclocal", "3ENQG1WyWv_xNKu4sKueWk3_IIMFNi8xhMUm3fLmfu0E4zOC9mstx7HeSG_D7RSu");
					return getAmount(uuid);
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
			try {
				con = DriverManager.getConnection("jdbc:mysql://192.99.45.178/tokens", "bcmclocal", "3ENQG1WyWv_xNKu4sKueWk3_IIMFNi8xhMUm3fLmfu0E4zOC9mstx7HeSG_D7RSu");
				return getAmount(uuid);
			} catch (SQLException e1) {
				System.out.println("MySQL re-connection failed!");
				e1.printStackTrace();
			}
		}
		return 0;
    }
	
	public void addAmount(UUID uuid, int amount){
		int old = getAmount(uuid);
		setAmount(uuid, old + amount);
	}
	
	public boolean takeAmount(UUID uuid, int amount){
		int old = getAmount(uuid);
		if (old - amount < 0) return false;
		setAmount(uuid, old - amount);
		return true;
	}
}
