package main.java.com.barancode.choiceuserdata;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import main.java.com.barancode.choiceuserdata.database.DatabaseIP;
import main.java.com.barancode.choiceuserdata.database.DatabaseUUID;

public class UserAPI {
	
	@SuppressWarnings("deprecation")
	public static UUID getUUID(String name){
		OfflinePlayer op = Bukkit.getOfflinePlayer(name);
		if (op.isOnline()) return op.getPlayer().getUniqueId();
		return DatabaseUUID.getUUID(name);
	}
	
	public static String getName(UUID uuid){
		if (isOnline(uuid)) return Bukkit.getPlayer(uuid).getName();
		return DatabaseUUID.getUsername(uuid);
	}
	public static String getName(String uuid){
		return getName(formatUUID(uuid));
	}
	
	public static String getIPFromUUID(UUID uuid){
		if (isOnline(uuid)) return Bukkit.getPlayer(uuid).getAddress().getHostString();
		return DatabaseIP.getIPFromUUID(uuid);
	}
	public static String getIPFromUUID(String uuid){
		return getIPFromUUID(formatUUID(uuid));
	}
	
	@SuppressWarnings("deprecation")
	public static String getIPFromName(String name){
		OfflinePlayer op = Bukkit.getOfflinePlayer(name);
		if (op.isOnline()) return Bukkit.getPlayer(name).getAddress().getHostString();
		return DatabaseIP.getIPFromName(name);
	}
	
	private static boolean isOnline(UUID uuid){
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p.getUniqueId().equals(uuid)) return true;
		}
		return false;
	}
	
	private static UUID formatUUID(String string){
		if (!string.contains("-")){
			string = string.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
		}
		return UUID.fromString(string);
	}
}
