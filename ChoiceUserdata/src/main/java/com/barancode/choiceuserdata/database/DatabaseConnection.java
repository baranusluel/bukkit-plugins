package main.java.com.barancode.choiceuserdata.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
	Connection con;
	String host,username,password,database;
	
	public void initialize(String host, String username, String password, String database){
		this.host = host; this.username = username; this.password = password; this.database = database;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC driver missing");
			e.printStackTrace();
			return;
		}
		
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
		} catch (SQLException e) {
			System.out.println("MySQL connection failed!");
			e.printStackTrace();
			return;
		}
		
		if (con != null) {
			Statement statement = null;
			try {
				statement = con.createStatement();
				statement.execute("CREATE TABLE IF NOT EXISTS players(id INT NOT NULL AUTO_INCREMENT, uuid VARCHAR(32), username VARCHAR(16), PRIMARY KEY (id));");
			} catch (SQLException e) {
				System.out.println("Initializing MySQL failed!");
				e.printStackTrace();
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}		 
			}
			// Separate try catch because we will handle errors differently
			try {
				statement = con.createStatement();
				statement.execute("ALTER TABLE players ADD ip VARCHAR(15);");
			} catch (SQLException e) {
				// Don't do anything because this giving an errors means
				// the column already exists
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}		 
			}
		} else {
			System.out.println("MySQL connection failed!");
		}
    }
	
	public void reconnect(){
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
		} catch (SQLException e) {
			System.out.println("MySQL connection failed!");
			e.printStackTrace();
			return;
		}
	}
	
	public void quit(){
		try {
			if (con != null) con.close();
		} catch (SQLException e) {
			System.out.println("Could not close() the connection");
			e.printStackTrace();
		}
	}
}
