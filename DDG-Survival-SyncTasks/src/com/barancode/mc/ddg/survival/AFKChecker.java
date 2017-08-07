package com.barancode.mc.ddg.survival;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class AFKChecker {
	Main plugin;
	ConcurrentHashMap<String, Integer> afkchecks = new ConcurrentHashMap<String, Integer>();
	
	@SuppressWarnings("deprecation")
	public AFKChecker(final Main plugin){
		this.plugin = plugin;
        plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run(){
            	for (Player p : Bukkit.getOnlinePlayers()){
            		if (p.hasPermission("survival.admin")) continue;
            		Location newloc = p.getLocation();
            		newloc.setX((int)newloc.getX());newloc.setY((int)newloc.getY());newloc.setZ((int)newloc.getZ());
            		
            		if (!plugin.locations.containsKey(p.getName())){
            			plugin.locations.put(p.getName(), newloc);
            			continue;
            		}
            		
            		Location oldloc = plugin.locations.get(p.getName());
            		if (newloc.getX() == oldloc.getX() && newloc.getY() == oldloc.getY() && newloc.getZ() == oldloc.getZ()){
            			int newvalue;
            			if (afkchecks.containsKey(p.getName())) newvalue = afkchecks.get(p.getName()) + 1;
            			else newvalue = 1;
            			afkchecks.put(p.getName(), newvalue);
            			if (newvalue > 150){
            				p.kickPlayer(plugin.utils.replace(plugin.getConfig().getString("afk-kick")));
            				afkchecks.remove(p.getName());
            				plugin.locations.remove(p.getName());
            			}
            		} else afkchecks.remove(p.getName());
            		
            		plugin.locations.put(p.getName(), newloc);
            	}
            }
        }, 0L, 40L);
	}
}
