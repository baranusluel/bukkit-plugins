package com.barancode.choicefriends.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.barancode.choicefriends.Main;
import com.barancode.choicefriends.Utils;

public class DatabaseFriendBlocks {
	
	public static List<UUID> getFriendBlocks(UUID uuid){
		return getFriendBlocks(uuid.toString().replaceAll("-", ""), 0);
	}
	
	private static List<UUID> getFriendBlocks(String player, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM friend_request_blocks WHERE uuid_blocker=\"" + player + "\" ORDER BY id DESC;");
				List<UUID> list = new ArrayList<UUID>();
				while (rs.next()){
					list.add(Utils.formatUUID(rs.getString("uuid_blocked")));
				}
				return list;
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					Main.db.reconnect();
					return getFriendBlocks(player, tries + 1);
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
				return getFriendBlocks(player, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
		return null;
    }
	
	public static void addFriendBlock(UUID uuid, UUID friend){
		addFriendBlock(uuid, friend, 0);
	}
	
	private static void addFriendBlock(UUID player, UUID friend, int tries){
		String playerUUID = player.toString().replaceAll("-", "");
		String friendUUID = friend.toString().replaceAll("-", "");
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				List<UUID> friends = getFriendBlocks(player);
				if (!friends.contains(friend)){
					statement = Main.db.con.createStatement();
					statement.execute("INSERT INTO friend_request_blocks(uuid_blocker, uuid_blocked) VALUES ('" + playerUUID + "', '" + friendUUID + "');");
				}
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					Main.db.reconnect();
					addFriendBlock(player, friend, tries + 1);
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
				addFriendBlock(player, friend, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
    }
	
	public static void removeFriendBlock(UUID uuid, UUID friend){
		removeFriendBlock(uuid, friend, 0);
	}
	
	private static void removeFriendBlock(UUID player, UUID friend, int tries){
		String playerUUID = player.toString().replaceAll("-", "");
		String friendUUID = friend.toString().replaceAll("-", "");
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				List<UUID> friends = getFriendBlocks(player);
				if (friends.contains(friend)){
					statement = Main.db.con.createStatement();
					statement.execute("DELETE FROM friend_request_blocks WHERE uuid_blocker='" + playerUUID + "' AND uuid_blocked='" + friendUUID + "';");
				}
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					Main.db.reconnect();
					removeFriendBlock(player, friend, tries + 1);
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
				removeFriendBlock(player, friend, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
    }
}
