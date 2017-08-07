package com.barancode.blockprotection.managers;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.barancode.blockprotection.Main;

public class PermissionManager {
	Main plugin;
	
	public PermissionManager(Main plugin){
		this.plugin = plugin;
	}
	
	public int getGroup(String s){
		PermissionUser user = PermissionsEx.getUser(s);
		for (int i = 1; i <= 50; i++){
			if (user.has("influence." + i)) return i;
		}
		return 1;
	}
}
