package com.barancode.choicefriends.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.barancode.choicefriends.Main;
import com.barancode.choicefriends.Utils;

public class DatabaseFriendRequest {
	
	public static List<UUID> getFriendRequests(UUID uuid){
		return getFriendRequests(uuid.toString().replaceAll("-", ""), 0);
	}
	
	private static List<UUID> getFriendRequests(String player, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM friend_requests WHERE uuid_target=\"" + player + "\" ORDER BY id DESC;");
				List<UUID> list = new ArrayList<UUID>();
				while (rs.next()){
					list.add(Utils.formatUUID(rs.getString("uuid_sender")));
				}
				return list;
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					Main.db.reconnect();
					return getFriendRequests(player, tries + 1);
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
				return getFriendRequests(player, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
		return null;
    }
	
	public static void addFriendRequest(UUID uuid, UUID friend){
		addFriendRequest(uuid, friend, 0);
	}
	
	private static void addFriendRequest(UUID player, UUID friend, int tries){
		String playerUUID = player.toString().replaceAll("-", "");
		String friendUUID = friend.toString().replaceAll("-", "");
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				List<UUID> friends = getFriendRequests(player);
				if (!friends.contains(friend)){
					statement = Main.db.con.createStatement();
					statement.execute("INSERT INTO friend_requests(uuid_target, uuid_sender) VALUES ('" + playerUUID + "', '" + friendUUID + "');");
				}
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					Main.db.reconnect();
					addFriendRequest(player, friend, tries + 1);
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
				addFriendRequest(player, friend, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
    }
	
	public static void removeFriendRequest(UUID uuid, UUID friend){
		removeFriendRequest(uuid, friend, 0);
	}
	
	private static void removeFriendRequest(UUID player, UUID friend, int tries){
		String playerUUID = player.toString().replaceAll("-", "");
		String friendUUID = friend.toString().replaceAll("-", "");
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				List<UUID> friends = getFriendRequests(player);
				if (friends.contains(friend)){
					statement = Main.db.con.createStatement();
					statement.execute("DELETE FROM friend_requests WHERE uuid_target='" + playerUUID + "' AND uuid_sender='" + friendUUID + "';");
				}
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					Main.db.reconnect();
					removeFriendRequest(player, friend, tries + 1);
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
				removeFriendRequest(player, friend, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
    }
}
