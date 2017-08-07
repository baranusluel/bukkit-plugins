package com.barancode.mc.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class TimeDatabase {
	
	public void increaseTime(UUID uuid){
		increaseTime(uuid, 0);
	}
	
	private void increaseTime(UUID uuid, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM time WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				if (rs.next()){
					statement.execute("UPDATE time SET time=" + (rs.getInt("time") + 1) + " WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				} else {
					statement.execute("INSERT INTO time(uuid, time) VALUES(\"" + uuid.toString().replaceAll("-", "") + "\", 1);");
				}
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					increaseTime(uuid, tries + 1);
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
				increaseTime(uuid, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
	
	public int getTime(UUID uuid){
		return getTime(uuid, 0);
	}
	
	private int getTime(UUID uuid, int tries){
		String player = uuid.toString().replaceAll("-", "");
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM time WHERE uuid=\"" + player + "\";");
				if (rs.next()){
					return rs.getInt("time");
				}
				return 0;
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					return getTime(uuid, tries + 1);
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
				return getTime(uuid, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
		return 0;
    }
}
