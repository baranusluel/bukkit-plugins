package com.barancode.mc.coincommands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Database {
	  Main plugin;
	  Connection con;

	  public Database(Main plugin)
	  {
	    this.plugin = plugin;
	  }
	  
	  public void initialize(){	 
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println("MySQL JDBC driver missing");
				e.printStackTrace();
				return;
			}
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
			} catch (SQLException e) {
				System.out.println("MySQL connection failed!");
				e.printStackTrace();
				return;
			}
		 
			if (con != null) {
				Statement statement = null;
				try {
					statement = con.createStatement();
					statement.execute("CREATE TABLE IF NOT EXISTS CommandCoins(uuid VARCHAR(16), coins INT, PRIMARY KEY (uuid));");
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
	  
	  public int getCoins(UUID uuid){
			if (con != null) {
				Statement statement = null;
				try {
					statement = con.createStatement();
					ResultSet rs = statement.executeQuery("SELECT * FROM CommandCoins WHERE uuid=\"" + uuid + "\";");
				    while (rs.next()){
				  	    return rs.getInt("coins");
				    }
				} catch (SQLException e) {
					System.out.println("MySQL failed!");
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
				System.out.println("MySQL connection failed! Attempting to re-initialize");
				initialize();
			}
			return 0;
	  }
	  
	  public void setCoins(UUID uuid, int coins){
			if (con != null) {
				Statement statement = null;
				try {
					statement = con.createStatement();
					statement.executeQuery("INSERT INTO CommandCoins(uuid, coins) VALUES (\"" + uuid + "\", " + coins + ") ON DUPLICATE KEY UPDATE coins=" + coins + ";");
				    
				} catch (SQLException e) {
					System.out.println("MySQL failed!");
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
				System.out.println("MySQL connection failed! Attempting to re-initialize");
				initialize();
			}
	  }
}
