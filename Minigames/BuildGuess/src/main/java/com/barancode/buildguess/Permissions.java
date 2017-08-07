package main.java.com.barancode.buildguess;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Permissions {
	// The PEX hook is used instead of player.hasPermission because the
	// plugin needs to check whether the player has the permission during the
	// asyncplayerpreloginevent, and you don't have a player object at that
	// time, only a username.
	public static boolean hasVIPJoin(String username){
		PermissionUser user = PermissionsEx.getUser(username);
	    return user.has("join.vip");
	}
	
	public static int getStartCoins(Player p){
		for (int i = 1; i <= 50; i++){
			if (p.hasPermission("coins." + i)) return i;
		}
		return ConfigValues.getDefaultCoins();
	}
}
