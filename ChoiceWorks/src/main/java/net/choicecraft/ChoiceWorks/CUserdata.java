package net.choicecraft.ChoiceWorks;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import net.choicecraft.ChoiceWorks.database.CDatabase;
import net.choicecraft.ChoiceWorks.database.CDatabaseResult;
import net.choicecraft.ChoiceWorks.exception.PlayerNotFoundException;
import net.choicecraft.ChoiceWorks.utils.CString;
import net.choicecraft.ChoiceWorks.utils.Utils;

public class CUserdata {

	/**
	 * Reference handle to the ChoiceWorks database.
	 */
	private static CDatabase m_Database;
	
	/**
	 * <b>[Internal ChoiceWorks Function - Do Not Use]</b><br>
	 * Initializes this class and prepares database.
	 * @param database Internal ChoiceWorks database handle.
	 */
	public static void initialize(CDatabase database)
	{
		m_Database = database;
		
		// check whether the database does not exist:
		if(!m_Database.tableExists("players")) {
			// create the `players` table:
			m_Database.query("CREATE TABLE `players`(`id` INT NOT NULL AUTO_INCREMENT, `uuid` VARCHAR(32), `username` VARCHAR(16), `ip` VARCHAR(15), PRIMARY KEY (`id`));");
		}
	}
	
	/**
	 * Updates the username field of the specified player or creates a new row
	 * when the uuid does not exist. This function must be called to make a new player.
	 * @param uuid The unique player id.
	 * @param username The current username of the player.
	 */
	public static void setUsername(UUID uuid, String username) {
		String formattedUUID = uuid.toString().replaceAll("-", "");
		boolean found = false;
		
		// find the player with the specified uuid:
		CDatabaseResult query = m_Database.query("SELECT * FROM `players` WHERE `uuid`='" + formattedUUID + "';");
		for(Map<String, String> row : query) {
			// we found a match inside the table.
			found = true;
			
			// check whether the stored username matches with the current username:
			if(row.get("username").equals(username)) return; // we are up to date!
			else{
				/* If you find a row (during the loop) that isn't up to date,
				 * update username associated with the uuid.
				 */
				m_Database.query("UPDATE `players` SET `username`='" + username + "' WHERE `uuid`='" + formattedUUID + "';");
				break;  // Exit the loop because we already updated the username for all rows with the given UUID (in case there are duplicates), don't need to do it again
			}
		}
		
		// when the player could not be found, insert new data:
		if(!found)
			m_Database.query("INSERT INTO `players`(uuid, username) VALUES('" + formattedUUID + "', '" + username + "');");
	}
	
	/**
	 * Tries to find the username associated with the specified uuid.
	 * @param uuid The uuid that is associated with the player.
	 * @return The username when found, otherwise throws an exception.
	 * @throws PlayerNotFoundException
	 */
	public static String getUsername(UUID uuid) throws PlayerNotFoundException {
		if (Utils.isOnline(uuid)) return Bukkit.getPlayer(uuid).getName();
		
		String formattedUUID = uuid.toString().replaceAll("-", "");
		boolean found = false;
		String username = "";
		
		// find the player with the specified uuid:
		CDatabaseResult query = m_Database.query("SELECT * FROM `players` WHERE `uuid`='" + formattedUUID + "';");
		for(Map<String, String> row : query) {
			// we found a match inside the table.
			found = true;
			username = row.get("username");
		}
		
		// when the player could not be found, throw exception:
		if(!found)
			throw new PlayerNotFoundException("No player associated with uuid: '" + uuid.toString() + "'");
		else
			return username;
	}
	
	/**
	 * Tries to find the uuid associated with the specified username.
	 * @param username The username that is associated with the player.
	 * @return The uuid when found, otherwise throws an exception.
	 * @throws PlayerNotFoundException
	 */
	@SuppressWarnings("deprecation")
	public static UUID getUUID(String username) throws PlayerNotFoundException {
		if (Utils.isOnline(username)) return Bukkit.getPlayer(username).getUniqueId();
		
		boolean found = false;
		UUID uuid = null;
		
		// find the player with the specified uuid:
		CDatabaseResult query = m_Database.query("SELECT * FROM `players` WHERE `username`='" + username + "';");
		for(Map<String, String> row : query) {
			// we found a match inside the table.
			found = true;
			uuid = CString.formatUUID(row.get("uuid"));
		}
		
		// when the player could not be found, throw exception:
		if(!found)
			throw new PlayerNotFoundException("No player associated with username: '" + username + "'");
		else
			return uuid;
	}
	
	/**
	 * Updates the IP field of the specified player.
	 * @param uuid The unique player id.
	 * @param ipaddress The current ip address of the player.
	 * @throws PlayerNotFoundException
	 */
	public static void setIP(UUID uuid, String ipaddress) throws PlayerNotFoundException {
		String formattedUUID = uuid.toString().replaceAll("-", "");
		boolean found = false;
		
		// find the player with the specified uuid:
		CDatabaseResult query = m_Database.query("SELECT * FROM `players` WHERE `uuid`='" + formattedUUID + "';");
		for(Map<String, String> row : query) {
			// we found a match inside the table.
			found = true;
			
			// check whether the stored ip matches with the current ip:
			if(row.get("ip").equals(ipaddress)) return; // we are up to date!
			else{
				/* If you find a row (during the loop) that isn't up to date,
				 * update ip associated with the uuid.
				 */
				m_Database.query("UPDATE `players` SET `ip`='" + ipaddress + "' WHERE `uuid`='" + formattedUUID + "';");
				break;  // Exit the loop because we already updated the IP for all rows with the given UUID (in case there are duplicates), don't need to do it again
			}
		}
		
		// when the player could not be found, throw exception:
		if(!found)
			throw new PlayerNotFoundException("No player associated with uuid: '" + uuid.toString() + "'");
	}
	
	/**
	 * Tries to find the ip address associated with the specified uuid.
	 * @param uuid The uuid that is associated with the player.
	 * @return The ip address when found, otherwise throws an exception.
	 * @throws PlayerNotFoundException
	 */
	public static String getIP(UUID uuid) throws PlayerNotFoundException {
		if (Utils.isOnline(uuid)) return Bukkit.getPlayer(uuid).getAddress().getHostString();
		
		String formattedUUID = uuid.toString().replaceAll("-", "");
		boolean found = false;
		String ipaddress = "";
		
		// find the player with the specified uuid:
		CDatabaseResult query = m_Database.query("SELECT * FROM `players` WHERE `uuid`='" + formattedUUID + "';");
		for(Map<String, String> row : query) {
			// we found a match inside the table.
			found = true;
			ipaddress = row.get("ip");
		}
		
		// when the player could not be found, throw exception:
		if(!found)
			throw new PlayerNotFoundException("No player associated with uuid: '" + uuid.toString() + "'");
		else
			return ipaddress;
	}
	
	/**
	 * Tries to find the ip address associated with the specified username.
	 * @param username The username that is associated with the player.
	 * @return The ip address when found, otherwise throws an exception.
	 * @throws PlayerNotFoundException
	 */
	@SuppressWarnings("deprecation")
	public static String getIP(String username) throws PlayerNotFoundException {
		if (Utils.isOnline(username)) return Bukkit.getPlayer(username).getAddress().getHostString();
		
		boolean found = false;
		String ipaddress = "";
		
		// find the player with the specified uuid:
		CDatabaseResult query = m_Database.query("SELECT * FROM `players` WHERE `username`='" + username + "';");
		for(Map<String, String> row : query) {
			// we found a match inside the table.
			found = true;
			ipaddress = row.get("ip");
		}
		
		// when the player could not be found, throw exception:
		if(!found)
			throw new PlayerNotFoundException("No player associated with username: '" + username + "'");
		else
			return ipaddress;
	}
}
