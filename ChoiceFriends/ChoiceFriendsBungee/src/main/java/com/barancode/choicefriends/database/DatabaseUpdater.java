package com.barancode.choicefriends.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.barancode.choicefriends.Main;
import com.barancode.choicefriends.Utils;

public class DatabaseUpdater {
	
	/*			// Separate try catch because we will handle errors differently
			try {statement.execute("ALTER TABLE players ADD ip VARCHAR(15);");} catch (SQLException e) {};
			try {statement.execute("ALTER TABLE players ADD friends TEXT;");} catch (SQLException e) {};
			try {statement.execute("ALTER TABLE players ADD friendrequests TEXT;");} catch (SQLException e) {};*/
	
	public static void updateTables(){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SHOW COLUMNS FROM players LIKE 'friends';");
				if (rs.next()){
					separateTablesUpdate();
				}
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) rs.close();
					if (statement != null) statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
    }
	
	private static void separateTablesUpdate(){
		System.out.println("\n==========\nChoiceFriends: Updating...\nThis may take a bit of time\n==========");
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM players;");
				while (rs.next()){
					UUID uuid = Utils.formatUUID(rs.getString("uuid"));
					String friendsString = rs.getString("friends");
					String friendrequestsString = rs.getString("friendrequests");
					if (friendsString != null && !friendsString.equals("")){
						for (String friend : friendsString.split(",")){
							if (friend.equals("")) continue;
							DatabaseFriend.addFriend(uuid, Utils.formatUUID(friend));
						}
					}
					if (friendrequestsString != null && !friendrequestsString.equals("")){
						for (String friendrequest : friendrequestsString.split(",")){
							if (friendrequest.equals("")) continue;
							DatabaseFriendRequest.addFriendRequest(uuid, Utils.formatUUID(friendrequest));
						}
					}
				}
				System.out.println("ChoiceFriends: Finished!");
			} catch (Exception e){
				System.out.println("There was an error! OMG OMG");
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) rs.close();
					if (statement != null) statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void dropOldColumns(){
		System.out.println("ChoiceFriends: Dropping columns");
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				statement = Main.db.con.createStatement();
				statement.execute("ALTER TABLE players DROP friends;");
				statement.execute("ALTER TABLE players DROP friendrequests;");
				System.out.println("ChoiceFriends: Finished!");
			} catch (Exception e){
				System.out.println("There was an error! OMG OMG");
				e.printStackTrace();
			} finally {
				try {
					if (statement != null) statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
