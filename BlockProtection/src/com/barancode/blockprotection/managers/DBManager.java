package com.barancode.blockprotection.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.barancode.blockprotection.Main;

public class DBManager {
	Main plugin;
	Connection con = null;	
	
	public DBManager(Main plugin){
		this.plugin = plugin;
	}
	
	public void initialize(){
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.out.println("SQLite JDBC driver missing");
			e.printStackTrace();
			return;
		}
		
		try {
			con = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/influence.db");
		} catch (SQLException e) {
			System.out.println("MySQL connection failed!");
			e.printStackTrace();
			return;
		}
		
		if (con != null) {
			Statement statement = null;
			try {
				statement = con.createStatement();
				statement.execute("CREATE TABLE IF NOT EXISTS influence(username VARCHAR(17), amount INT);");
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
			con = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/influence.db");
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
	
	
	public void setAmount(String username, int amount){
		setAmount(username, amount, 0);
	}
	
	@SuppressWarnings("deprecation")
	private void setAmount(final String username, final int amount, final int tries){
		plugin.scheduler.scheduleAsyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run(){
				if (con != null) {
					Statement statement = null;
					ResultSet rs = null;
					try {
						statement = con.createStatement();
						rs = statement.executeQuery("SELECT * FROM influence WHERE username=\"" + username + "\";");
						if (rs.next()){
							statement.execute("UPDATE influence SET amount=" + amount + " WHERE username=\"" + username + "\";");
						} else {
							statement.execute("INSERT INTO influence(username, amount) VALUES(\"" + username + "\", " + amount + ");");
						}
						
						
						plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable(){
							@Override
							public void run(){
								OfflinePlayer player = Bukkit.getOfflinePlayer(username);
								if (player.isOnline()) plugin.boardman.updateScoreboard(player.getPlayer());
							}
						});
						
						
					} catch (Exception e){
						if (tries < 3){
							reconnect();
							setAmount(username, amount, tries + 1);
						} else {
							System.out.println("Attempted to re-connect 3 times, all of them failed");
						}
					} finally {
						try {
							if (rs != null) rs.close();
							if (statement != null) statement.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				} else {
					if (tries < 3){
						reconnect();
						setAmount(username, amount, tries + 1);
					} else {
						System.out.println("Attempted to re-connect 3 times, all of them failed");
					}
				}
			}
		});
    }
	
	public int getAmount(String username){
		return getAmount(username, 0);
	}
	
	private int getAmount(String username, int tries){
		if (con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = con.createStatement();
				rs = statement.executeQuery("SELECT * FROM influence WHERE username=\"" + username + "\";");
				if (rs.next()){
					return rs.getInt("amount");
				}
				return 0;
			} catch (Exception e){
				if (tries < 3){
					reconnect();
					return getAmount(username, tries + 1);
				} else {
					System.out.println("Attempted to re-connect 3 times, all of them failed");
				}
			} finally {
				try {
					if (rs != null) rs.close();
					if (statement != null) statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (tries < 3){
				reconnect();
				return getAmount(username, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
		return 0;
    }
	
	public void addAmount(String username, int amount){
		int newAmount = getAmount(username) + amount;
		if (newAmount > 500) return;
		setAmount(username, newAmount);
	}
	
	public boolean takeAmount(String username, int amount){
		int old = getAmount(username);
		if (old - amount < 0) return false;
		setAmount(username, old - amount);
		return true;
	}
}
