package com.barancode.blockprotection.managers;

import java.util.List;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.ParseResult;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.barancode.blockprotection.Main;

public class CoreProtectManager {
	
	Main plugin;
	CoreProtectAPI co = null;
	
	public CoreProtectManager(Main plugin){
		this.plugin = plugin;
	}
	
	public CoreProtectAPI getCoreProtect() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("CoreProtect");
		if (plugin == null || !(plugin instanceof CoreProtect)) {
		    return null;
		}
		CoreProtectAPI CoreProtect = ((CoreProtect)plugin).getAPI();
		if (CoreProtect.APIVersion() < 2){
		    return null;
		}
		co = CoreProtect;
		return CoreProtect;
	}
	
	public String getPlacer(Block b){
		List<String[]> lookup = co.blockLookup(b, 0);
		for (String[] array : lookup){
			ParseResult pr = co.parseResult(array);
			if (pr.getActionId() == 1) return pr.getPlayer();
		}
		return "";
	}
}
