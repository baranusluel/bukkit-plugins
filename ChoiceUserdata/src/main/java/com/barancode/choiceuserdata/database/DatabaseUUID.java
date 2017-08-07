package main.java.com.barancode.choiceuserdata.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import main.java.com.barancode.choiceuserdata.ChoiceUserdata;

public class DatabaseUUID {
	
	public static void setPair(UUID uuid, String username){
		setPair(uuid, username, 0);
	}
	
	private static void setPair(UUID uuid, String username, int tries){
		if (ChoiceUserdata.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			String formattedUUID = uuid.toString().replaceAll("-", "");
			try {
				statement = ChoiceUserdata.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM players WHERE uuid=\"" + formattedUUID + "\";");
				if (rs.next()){
					if (rs.getString("username").equals(username)) return;
					else statement.execute("UPDATE players SET username=\"" + username + "\" WHERE uuid=\"" + formattedUUID + "\";");
				} else {
					statement.execute("INSERT INTO players(uuid, username) VALUES(\"" + formattedUUID + "\", \"" + username + "\");");
				}
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					ChoiceUserdata.db.reconnect();
					setPair(uuid, username, tries + 1);
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
				ChoiceUserdata.db.reconnect();
				setPair(uuid, username, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
    }
	
	public static UUID getUUID(String username){
		String s = getValue(username, "username", "uuid", 0);
		if (!s.equals("")){
			return UUID.fromString(s.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
		} else {
			return null;
		}
	}
	
	public static String getUsername(UUID uuid){
		return getValue(uuid.toString(), "uuid", "username", 0);
	}
	
	private static String getValue(String valueGiven, String typeGiven, String typeWanted, int tries){
		if (typeGiven.equals("uuid")) valueGiven = valueGiven.replaceAll("-", "");
		if (ChoiceUserdata.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = ChoiceUserdata.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM players WHERE " + typeGiven + "=\"" + valueGiven + "\" ORDER BY id DESC;");
				if (rs.next()){
					return rs.getString(typeWanted);
				}
				return "";
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					ChoiceUserdata.db.reconnect();
					return getValue(valueGiven, typeGiven, typeWanted, tries + 1);
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
				ChoiceUserdata.db.reconnect();
				return getValue(valueGiven, typeGiven, typeWanted, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
		return "";
    }
}
