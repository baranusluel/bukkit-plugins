package com.barancode.mc.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class CommandDatabase {
	public void addCommand(String command){
		addCommand(command, 0);
	}
	
	private void addCommand(String command, int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			try {
				statement = Main.db.con.createStatement();
				UUID id = UUID.randomUUID();
				statement.execute("INSERT INTO commands(command, id) VALUES(\"" + command + "\", \"" + id.toString() + "\");");
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 3){
					Main.db.reconnect();
					addCommand(command, tries + 1);
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
				addCommand(command, tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
    }
	
	public HashMap<String, UUID> getCommands(){
		return getCommands(0);
	}
	
	private HashMap<String, UUID> getCommands(int tries){
		if (Main.db.con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				HashMap<String, UUID> commands = new HashMap<String, UUID>();
				statement = Main.db.con.createStatement();
				rs = statement.executeQuery("SELECT * FROM commands;");
				while (rs.next()){
					commands.put(rs.getString("command"), UUID.fromString(rs.getString("id")));
				}
				return commands;
			} catch (Exception e){
				e.printStackTrace();
				if (tries < 3){
					Main.db.reconnect();
					return getCommands(tries + 1);
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
				return getCommands(tries + 1);
			} else {
				System.out.println("Attempted to re-connect 3 times, all of them failed");
			}
		}
		return null;
    }
}
