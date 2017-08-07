package com.barancode.choicefriends.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.barancode.choicefriends.Main;
import com.barancode.choicefriends.Utils;

public class DatabaseFriend {
	
	public static List<UUID> getFriends(UUID uuid){
		return getFriends(uuid.toString().replaceAll("-", ""), 0);
	}
	
	private static List<UUID> getFriends(String player, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM friends WHERE uuid=\"" + player + "\" ORDER BY id DESC;");
				List<UUID> list = new ArrayList<UUID>();
				while (rs.next()){
					list.add(Utils.formatUUID(rs.getString("uuid_friend")));
				}
				return list;
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					Main.db.reconnect();
					return getFriends(player, tries + 1);
				} else {
					System.out.println("Attempted to re-connect but failed");
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
			if (tries < 1){
				Main.db.reconnect();
				return getFriends(player, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
		return null;
    }
	
	public static void addFriend(UUID uuid, UUID friend){
		addFriend(uuid, friend, 0);
	}
	
	private static void addFriend(UUID player, UUID friend, int tries){
		String playerUUID = player.toString().replaceAll("-", "");
		String friendUUID = friend.toString().replaceAll("-", "");
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				List<UUID> friends = getFriends(player);
				if (!friends.contains(friend)){
					statement = Main.db.con.createStatement();
					statement.execute("INSERT INTO friends(uuid, uuid_friend) VALUES (\"" + playerUUID + "\", \"" + friendUUID + "\");");
				}
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					Main.db.reconnect();
					addFriend(player, friend, tries + 1);
				} else {
					System.out.println("Attempted to re-connect but failed");
				}
			} finally {
				try {
					if (statement != null) statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (tries < 1){
				Main.db.reconnect();
				addFriend(player, friend, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
    }
	
	public static void removeFriend(UUID uuid, UUID friend){
		removeFriend(uuid, friend, 0);
	}
	
	private static void removeFriend(UUID player, UUID friend, int tries){
		String playerUUID = player.toString().replaceAll("-", "");
		String friendUUID = friend.toString().replaceAll("-", "");
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				List<UUID> friends = getFriends(player);
				if (friends.contains(friend)){
					statement = Main.db.con.createStatement();
					statement.execute("DELETE FROM friends WHERE uuid='" + playerUUID + "' AND uuid_friend='" + friendUUID + "';");
				}
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					Main.db.reconnect();
					removeFriend(player, friend, tries + 1);
				} else {
					System.out.println("Attempted to re-connect but failed");
				}
			} finally {
				try {
					if (statement != null) statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (tries < 1){
				Main.db.reconnect();
				removeFriend(player, friend, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
    }
}
