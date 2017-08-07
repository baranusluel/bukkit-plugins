package com.barancode.mc.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class TokenDatabase {
	
	public void setAmount(UUID uuid, int amount){
		setAmount(uuid, amount, 0);
	}
	
	private void setAmount(UUID uuid, int amount, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM tokens WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				if (rs.next()){
					statement.execute("UPDATE tokens SET amount=" + amount + " WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				} else {
					statement.execute("INSERT INTO tokens(uuid, amount) VALUES(\"" + uuid.toString().replaceAll("-", "") + "\", " + amount + ");");
				}
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					setAmount(uuid, amount, tries + 1);
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
				Main.db.reconnect();
				setAmount(uuid, amount, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
	
	public int getAmount(UUID uuid){
		return getAmount(uuid.toString().replaceAll("-", ""), 0);
	}
	
	private int getAmount(String uuid, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM tokens WHERE uuid=\"" + uuid + "\";");
				if (rs.next()){
					return rs.getInt("amount");
				}
				return 0;
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					return getAmount(uuid, tries + 1);
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
				Main.db.reconnect();
				return getAmount(uuid, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
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
