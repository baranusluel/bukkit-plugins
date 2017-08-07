package net.choicecraft.ChoiceWorks.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Utils {
	public static boolean isOnline(UUID uuid){ // Don't think there's a better way to do this, right? If I remember correctly, getting an offline player from UUID isn't safe
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p.getUniqueId().equals(uuid)) return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isOnline(String name){
		return Bukkit.getOfflinePlayer(name).isOnline();
	}
	
	/**
	 * Optimized routine to find all players within radius relative to a player's location and world (compared to getEntitiesByClass).
	 * @param player The player to search at.
	 * @param distance The radius to find players in.
	 * @return A list of players found in the specified radius relative to the player.
	 */
	public static List<Player> getPlayersWithin(Player player, int distance) {
		List<Player> res = new ArrayList<Player>();
		int d2 = distance * distance;
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (p.getWorld() == player.getWorld()
					&& p.getLocation().distanceSquared(player.getLocation()) <= d2) {
				res.add(p);
			}
		}
		return res;
	}
}
