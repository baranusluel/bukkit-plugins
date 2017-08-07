package com.barancode.mc.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class UUIDDatabase {
	
	public void setPair(UUID uuid, String username){
		setPair(uuid, username, 0);
	}
	
	private void setPair(UUID uuid, String username, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM uuid WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				if (rs.next()){
					if (rs.getString("username").equals(username)) return;
					else statement.execute("UPDATE uuid SET username=\"" + username + "\" WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				} else {
					statement.execute("INSERT INTO uuid(uuid, username) VALUES(\"" + uuid.toString().replaceAll("-", "") + "\", \"" + username + "\");");
				}
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					setPair(uuid, username, tries + 1);
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
				setPair(uuid, username, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
	
	public UUID getUUID(String username){
		String s = getValue(username, "username", "uuid", 0).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
		if (!s.equals("")){
			return UUID.fromString(s);
		} else {
			return null;
		}
	}
	
	public String getUsername(UUID uuid){
		return getValue(uuid.toString(), "uuid", "username", 0);
	}
	
	private String getValue(String valueGiven, String typeGiven, String typeWanted, int tries){
		if (typeGiven.equals("uuid")) valueGiven = valueGiven.replaceAll("-", "");
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM uuid WHERE " + typeGiven + "=\"" + valueGiven + "\";");
				if (rs.next()){
					return rs.getString(typeWanted);
				}
				return "";
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					return getValue(valueGiven, typeGiven, typeWanted, tries + 1);
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
				return getValue(valueGiven, typeGiven, typeWanted, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
		return "";
    }
}
