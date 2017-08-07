package net.choicecraft.ChoiceWorks;

import java.util.UUID;

import net.choicecraft.ChoiceWorks.exception.PlayerNotFoundException;
import net.choicecraft.ChoiceWorks.utils.CString;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * <b>Do not use, instead use CUserdata directly!</b>
 */
@Deprecated
public class UserAPI {
	/**
	 * <b>Do not use, instead use CUserdata directly!</b>
	 */
	@Deprecated
	public static UUID getUUID(String name) throws PlayerNotFoundException{
		OfflinePlayer op = Bukkit.getOfflinePlayer(name);
		if (op.isOnline()) return op.getPlayer().getUniqueId();
		return CUserdata.getUUID(name);
	}
	
	/**
	 * <b>Do not use, instead use CUserdata directly!</b>
	 */
	@Deprecated
	public static String getName(UUID uuid) throws PlayerNotFoundException{
		if (isOnline(uuid)) return Bukkit.getPlayer(uuid).getName();
		return CUserdata.getUsername(uuid);
	}
	
	/**
	 * <b>Do not use, instead use CUserdata directly!</b>
	 */
	@Deprecated
	public static String getName(String uuid) throws PlayerNotFoundException{
		return getName(CString.formatUUID(uuid));
	}
	
	/**
	 * <b>Do not use, instead use CUserdata directly!</b>
	 */
	@Deprecated
	public static String getIPFromUUID(UUID uuid) throws PlayerNotFoundException{
		if (isOnline(uuid)) return Bukkit.getPlayer(uuid).getAddress().getHostString();
		return CUserdata.getIP(uuid);
	}
	
	/**
	 * <b>Do not use, instead use CUserdata directly!</b>
	 */
	@Deprecated
	public static String getIPFromUUID(String uuid) throws PlayerNotFoundException{
		return getIPFromUUID(CString.formatUUID(uuid));
	}
	
	/**
	 * <b>Do not use, instead use CUserdata directly!</b>
	 */
	@Deprecated
	public static String getIPFromName(String name) throws PlayerNotFoundException{
		OfflinePlayer op = Bukkit.getOfflinePlayer(name);
		if (op.isOnline()) return Bukkit.getPlayer(name).getAddress().getHostString();
		return CUserdata.getIP(name);
	}
	
	/**
	 * <b>Do not use, instead use CUserdata directly!</b>
	 */
	@Deprecated
	private static boolean isOnline(UUID uuid){
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p.getUniqueId().equals(uuid)) return true;
		}
		return false;
	}
}
