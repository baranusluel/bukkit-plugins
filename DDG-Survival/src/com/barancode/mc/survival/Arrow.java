package com.barancode.mc.survival;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Arrow {
	
	  Main plugin;

	  public Arrow(Main plugin)
	  {
	    this.plugin = plugin;
	  }
	  
	public void start(){
	        plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
	            @Override
	            public void run(){
	    			for (UUID u : plugin.activearrows.keySet()){
	    				boolean contains = false;
	    				for (Player temp : Bukkit.getOnlinePlayers()){
	    					if (temp.getUniqueId().equals(u)) contains = true;
	    				}
	    				if (!contains) continue;
	    				Player player = Bukkit.getPlayer(u);
	    				Location targetloc = plugin.activearrows.get(u);
	    				Location playerloc = player.getLocation();
	    				targetloc.setY(playerloc.getY());
	    				double xdiff = targetloc.getX() - playerloc.getX();
	    				double zdiff = targetloc.getZ() - playerloc.getZ();
	    				double distance = targetloc.distance(playerloc);
	    				if (distance < 16) continue;
	    				
	    				for (int i = 1; i < 16; i++){
	    					double ratio = i / distance;
	    					double x = ratio * xdiff + playerloc.getX();
	    					double z = ratio * zdiff + playerloc.getZ();
	    					ParticleEffect.RED_DUST.display(new Location(targetloc.getWorld(), x, playerloc.getY() + 4, z), 0.2F, 0F, 0.2F, 0.0F, 20, player);
	    				}
	    			}
	            }
	        }, 0L, 10L);
	  }
}
