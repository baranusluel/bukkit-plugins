package main.java.com.barancode.choiceuserdata.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import main.java.com.barancode.authbypass.Main;

public class DatabaseIP {
	
	public static String getIPFromUUID(UUID uuid){
		return getIP(uuid.toString().replaceAll("-", ""), "uuid", 0);
	}
	
	public static String getIPFromName(String username){
		return getIP(username, "username", 0);
	}
	
	private static String getIP(String player, String type, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM players WHERE " + type + "=\"" + player + "\" ORDER BY id DESC;");
				if (rs.next()){
					String ipString = rs.getString("ip");
					if (ipString != null) return ipString;
				}
				return "";
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 1){
					Main.db.reconnect();
					return getIP(player, type, tries + 1);
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
				return getIP(player, type, tries + 1);
			} else {
				System.out.println("Attempted to re-connect but failed");
			}
		}
		return "";
    }
}
