package com.barancode.blockprotection;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AFKChecker {
	Main plugin;
	ConcurrentHashMap<String, Integer> afkchecks = new ConcurrentHashMap<String, Integer>();
	
	public AFKChecker(final Main plugin){
		this.plugin = plugin;
        plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run(){
            	for (Player p : Bukkit.getOnlinePlayers()){
            		if (plugin.override.contains(p.getName())) continue;
            		Location newloc = p.getLocation();
            		newloc.setX((int)newloc.getX());newloc.setY((int)newloc.getY());newloc.setZ((int)newloc.getZ());
            		
            		if (!plugin.afklocations.containsKey(p.getName())){
            			plugin.afklocations.put(p.getName(), newloc);
            			continue;
            		}
            		
            		Location oldloc = plugin.afklocations.get(p.getName());
            		if (newloc.getX() == oldloc.getX() && newloc.getY() == oldloc.getY() && newloc.getZ() == oldloc.getZ()){
            			int newvalue;
            			if (afkchecks.containsKey(p.getName())) newvalue = afkchecks.get(p.getName()) + 1;
            			else newvalue = 1;
            			afkchecks.put(p.getName(), newvalue);
            			if (newvalue > 150){
            				p.kickPlayer(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("afk-kick")));
            				afkchecks.remove(p.getName());
            				plugin.afklocations.remove(p.getName());
            			}
            		} else afkchecks.remove(p.getName());
            		
            		plugin.afklocations.put(p.getName(), newloc);
            	}
            }
        }, 0L, 40L);
	}
}
