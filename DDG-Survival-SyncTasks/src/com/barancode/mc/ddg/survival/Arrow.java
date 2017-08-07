package com.barancode.mc.ddg.survival;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Arrow {
	
	  Main plugin;

	  public Arrow(Main plugin)
	  {
	    this.plugin = plugin;
	  }
	  
	@SuppressWarnings("deprecation")
	public void start(){
	        plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
	            @Override
	            public void run(){
	    			for (String s : plugin.arrows.keySet()){
	    				if (!Bukkit.getOfflinePlayer(s).isOnline()) continue;
	    				Player player = Bukkit.getPlayer(s);
	    				Location targetloc = plugin.arrows.get(s);
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
	    					ParticleEffects.sendToPlayer(ParticleEffects.REDSTONE_DUST, player, new Location(Bukkit.getWorld("world"), x, playerloc.getY() + 4, z), 0.2F, 0F, 0.2F, 0.0F, 20);
	    				}
	    			}
	            }
	        }, 0L, 10L);
	  }
}
