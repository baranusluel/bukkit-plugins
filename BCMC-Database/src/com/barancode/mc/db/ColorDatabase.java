package com.barancode.mc.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class ColorDatabase {
	
	public void setColor(UUID uuid, int color){
		setColor(uuid, color, 0);
	}
	
	private void setColor(UUID uuid, int color, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM colors WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				if (rs.next()){
					statement.execute("UPDATE colors SET color=" + color + " WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				} else {
					statement.execute("INSERT INTO colors(color, uuid) VALUES(" + color + ", \"" + uuid.toString().replaceAll("-", "") + "\");");
				}
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					setColor(uuid, color, tries + 1);
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
				setColor(uuid, color, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
	
	public int getColor(UUID uuid){
		return getColor(uuid, 0);
	}
	
	private int getColor(UUID uuid, int tries){
		String player = uuid.toString().replaceAll("-", "");
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM colors WHERE uuid=\"" + player + "\";");
				if (rs.next()){
					return rs.getInt("color");
				}
				return 114;
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					return getColor(uuid, tries + 1);
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
				return getColor(uuid, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
		return 114;
    }
}
