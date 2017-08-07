package net.choicecraft.ChoiceWorks.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.choicecraft.ChoiceWorks.ChoiceWorks;

public class CDatabase implements AutoCloseable {
	// database information:
	private static String s_Hostname;
	private static String s_Username;
	private static String s_Password;
	private static String s_Database;
	
	// state information:
	private static Boolean s_IsAvailable;
	
	/**
	 * <b>[Internal ChoiceWorks Function - Do Not Use]</b><br>
	 * Sets the hostname for the database connection.
	 * @param hostname The hostname to be used.
	 */
	public static void setHostname(String hostname)
	{
		s_Hostname = hostname;
	}
	
	/**
	 * Gets the hostname used in the database connection.
	 * @return The hostname.
	 */
	private static String getHostname()
	{
		return s_Hostname;
	}
	
	/**
	 * <b>[Internal ChoiceWorks Function - Do Not Use]</b><br>
	 * Sets the username for the database connection.
	 * @param username The username to be used.
	 */
	public static void setUsername(String username)
	{
		s_Username = username;
	}
	
	/**
	 * Gets the username used in the database connection.
	 * @return The username.
	 */
	private static String getUsername()
	{
		return s_Username;
	}
	
	/**
	 * <b>[Internal ChoiceWorks Function - Do Not Use]</b><br>
	 * Sets the password for the database connection.
	 * @param username The password to be used.
	 */
	public static void setPassword(String password)
	{
		s_Password = password;
	}
	
	/**
	 * Gets the password used in the database connection.
	 * @return The password.
	 */
	private static String getPassword()
	{
		return s_Password;
	}
	
	/**
	 * <b>[Internal ChoiceWorks Function - Do Not Use]</b><br>
	 * Sets the database name for the database connection.
	 * @param username The database name to be used.
	 */
	public static void setDatabase(String database)
	{
		s_Database = database;
	}
	
	/**
	 * Gets the database name used in the database connection.
	 * @return The database name.
	 */
	private static String getDatabase()
	{
		return s_Database;
	}
	
	/**
	 * <b>[Internal ChoiceWorks Function - Do Not Use]</b><br>
	 * Initializes this class and prepares database connections.
	 * @return True on success, False otherwise.
	 */
	public static boolean initialize()
	{
		s_IsAvailable = true;
		
		// check: ensure the required drivers are available:
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			ChoiceWorks.print("MySQL JDBC driver missing!", CDatabase.class.getSimpleName());
			s_IsAvailable = false;
			return false;
		}
		
		return true;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// connection handle:
	private Connection m_Connection;
	private Boolean m_IsConnected;
	
	/**
	 * Creates a new MySQL database connection.
	 */
	public CDatabase()
	{
		if(!s_IsAvailable) return;
		
		m_IsConnected = connect();
	}
	
	/**
	 * This function is automatically called to disconnect from the database and clean-up.
	 */
	@Override
	public void close() throws IOException {
		disconnect();
		m_IsConnected = false;
	}
	
	/**
	 * Creates or refreshes the connection to the MySQL database.
	 * @return True when successful or false if it fails to connect.
	 */
	private boolean connect()
	{
		if(!s_IsAvailable) return false;
		
		try {
			m_Connection = DriverManager.getConnection("jdbc:mysql://" + getHostname() + "/" + getDatabase(), getUsername(), getPassword());
		} catch (SQLException e) {
			ChoiceWorks.print("Unable to connect to the MySQL database!", CDatabase.class.getSimpleName());
			return false;
		}
		
		return (m_Connection != null);
	}
	
	/**
	 * Closes the connection with the MySQL database.
	 */
	private void disconnect()
	{
		if(!s_IsAvailable) return;
		
		try {
			if (m_Connection != null) {
				m_Connection.close();
				m_Connection = null;
			}
		} catch (SQLException e) {
			ChoiceWorks.print("Unable to close the MySQL connection!", CDatabase.class.getSimpleName());
			return; // assume we are disconnected already.
		}
		
		return;
	}
	
	/**
	 * Public method for reconnecting
	 * @return True when successful or false if it fails to connect.
	 */
	public boolean reconnect(){
		disconnect();
		m_IsConnected = connect();
		return m_IsConnected;
	}
	
	/**
	 * The raw internal connection handle for full manual control.
	 * @return The java.sql.Connection handle.
	 */
	public Connection getConnection()
	{
		if(!s_IsAvailable) return null;
		if(!m_IsConnected) return null;
		return m_Connection;
	}
	
	/**
	 * Checks whether a database table exists.
	 * @param name The name of the table.
	 * @return True when it exists, false otherwise.
	 */
	public boolean tableExists(String name)
	{
		CDatabaseResult query = query("SHOW TABLES LIKE '" + name + "'");
		return (query.getRowCount() != 0);
	}
	
	public CDatabaseResult query(String query)
	{
		return query(query, false);
	}
	
	/**
	 * Executes a MySQL query string.
	 * @param query The MySQL statement to be executed.
	 * @return The CDatabaseResult, guaranteed to never be null.
	 */
	private CDatabaseResult query(String query, boolean retry)
	{
		if(!s_IsAvailable) return new CDatabaseResult(); // NULL
		if(m_Connection == null){
			ChoiceWorks.print("Database connection was lost, query failed", CDatabase.class.getSimpleName());
			if (!retry){
				if(!reconnect()) return new CDatabaseResult(); // NULL
				ChoiceWorks.print("Reconnected, retrying the query", CDatabase.class.getSimpleName());
				return query(query, true);
			}
		}
		//if(!reconnect()) return new CDatabaseResult(); // NULL
		
		// create a new MySQL statement:
		Statement statement = null;
		try {
			statement = m_Connection.createStatement();
		} catch (SQLException e) {
			return new CDatabaseResult(e); // EXCEPTION
		}
		
		// attempt to execute the query:
		try {
			statement.execute(query);
			
			// depending on the type of query, may not have a ResultSet.
			CDatabaseResult result;
			ResultSet res = statement.getResultSet();
			
			// store as much information as we can at this point:
			if(res == null) {
				result = new CDatabaseResult(statement.getUpdateCount());
			} else {
				result = new CDatabaseResult(res);
			}
			
			// clean up resources, CDatabaseResult is already done.
			try { if (res != null) res.close(); } catch (Exception e) {};
			try { if (statement != null) statement.close(); } catch (Exception e) {};
			return result;
		} catch (SQLException e) {
			ChoiceWorks.print("Had an exception performing a database query", CDatabase.class.getSimpleName());
			if (!retry){
				if(!reconnect()) return new CDatabaseResult(); // NULL
				ChoiceWorks.print("Reconnected, retrying the query", CDatabase.class.getSimpleName());
				return query(query, true);
			}
			e.printStackTrace();
			return new CDatabaseResult(e); // EXCEPTION
		}
	}
}
