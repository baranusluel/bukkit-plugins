package com.barancode.mc.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class VoteDatabase {
	
	public void setVotes(UUID uuid, int amount){
		setVotes(uuid, amount, 0);
	}
	
	private void setVotes(UUID uuid, int amount, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM votes WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				if (rs.next()){
					statement.execute("UPDATE votes SET amount=" + amount + " WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				} else {
					statement.execute("INSERT INTO votes(uuid, amount) VALUES(\"" + uuid.toString().replaceAll("-", "") + "\", " + amount + ");");
				}
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					setVotes(uuid, amount, tries + 1);
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
				setVotes(uuid, amount, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
	
	public int getVotes(UUID uuid){
		return getVotes(uuid.toString().replaceAll("-", ""), 0);
	}
	
	private int getVotes(String uuid, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM votes WHERE uuid=\"" + uuid + "\";");
				if (rs.next()){
					return rs.getInt("amount");
				}
				return 0;
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					return getVotes(uuid, tries + 1);
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
				return getVotes(uuid, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
		return 0;
    }
	
	public void addVote(UUID uuid){
		int old = getVotes(uuid);
		setVotes(uuid, old + 1);
	}
	
	public void resetVotes(){
		resetVotes(0);
	}
	
	private void resetVotes(int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				statement = Main.db.con.createStatement();
				statement.executeQuery("DELETE FROM votes;");
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					resetVotes(tries + 1);
				} else {
					System.out.println("Attempted to re-connect 3 times, all of them failed");
				}
			} finally {
				try {
					if (statement != null) statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (tries < 3){
				Main.db.reconnect();
				resetVotes(tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
}
