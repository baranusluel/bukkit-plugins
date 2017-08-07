package com.barancode.mc.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppDatabase {
	public void submitApp(UUID uuid, String text, long time){
		submitApp(uuid, text, time, 0);
	}
	
	private void submitApp(UUID uuid, String text, long time, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				statement = Main.db.con.createStatement();
				statement.execute("INSERT INTO apps(uuid, text, time, votes) VALUES(\"" + uuid.toString().replaceAll("-", "") + "\", \"" + text + "\", " + time + ", 0);");
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					submitApp(uuid, text, time, tries + 1);
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
				submitApp(uuid, text, time, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
	
	public void updateText(UUID uuid, String text){
		updateText(uuid, text, 0);
	}
	
	private void updateText(UUID uuid, String text, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				statement = Main.db.con.createStatement();
				statement.execute("UPDATE apps SET text=\"" + text + "\" WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					updateText(uuid, text, tries + 1);
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
				updateText(uuid, text, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
	
	public void deleteApp(UUID uuid){
		deleteApp(uuid, 0);
	}
	
	private void deleteApp(UUID uuid, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				statement = Main.db.con.createStatement();
				statement.execute("DELETE FROM apps WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					deleteApp(uuid, tries + 1);
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
				deleteApp(uuid, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
	
	public void vote(UUID uuid, boolean up){
		vote(uuid, up, 0);
	}
	
	private void vote(UUID uuid, boolean up, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				statement = Main.db.con.createStatement();
				if (up) statement.execute("UPDATE apps SET votes=" + (getVotes(uuid) + 1) + " WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				else statement.execute("UPDATE apps SET votes=" + (getVotes(uuid) - 1) + " WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					vote(uuid, up, tries + 1);
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
				vote(uuid, up, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
	
	public int getVotes(UUID uuid){
		return getVotes(uuid, 0);
	}
	
	private int getVotes(UUID uuid, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM apps WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				if (rs.next()){
					return rs.getInt("votes");
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
	
	public List<AppObject> getApps(){
		return getApps(0);
	}
	
	private List<AppObject> getApps(int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM apps ORDER BY time ASC;");
				List<AppObject> apps = new ArrayList<AppObject>();
				while (rs.next()){
					apps.add(new AppObject(UUID.fromString(rs.getString("uuid").replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")), rs.getString("text"), rs.getLong("time")));
				}
				return apps;
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 3){
					Main.db.reconnect();
					return getApps(tries + 1);
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
				return getApps(tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
		return null;
	}
	
	public int getAppCount(){
		return getAppCount(0);
	}
	
	private int getAppCount(int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM apps;");
				int count = 0;
				while (rs.next()){
					count++;
				}
				return count;
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					return getAppCount(tries + 1);
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
				return getAppCount(tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
		return 0;
    }
	
	public boolean appExists(UUID uuid){
		return appExists(uuid, 0);
	}
	
	private boolean appExists(UUID uuid, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM apps WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				if (rs.next()){
					return true;
				}
				return false;
			} catch (Exception e){
				if (tries < 3){
					Main.db.reconnect();
					return appExists(uuid, tries + 1);
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
				return appExists(uuid, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
		return false;
    }
	
	public AppObject getApp(UUID uuid){
		return getApp(uuid, 0);
	}
	
	private AppObject getApp(UUID uuid, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM apps WHERE uuid=\"" + uuid.toString().replaceAll("-", "") + "\";");
				if (rs.next()){
					return new AppObject(uuid, rs.getString("text"), rs.getLong("time"));
				}
				return null;
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 3){
					Main.db.reconnect();
					return getApp(uuid, tries + 1);
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
				return getApp(uuid, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
		return null;
    }
}
