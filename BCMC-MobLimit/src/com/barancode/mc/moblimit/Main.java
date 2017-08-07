package com.barancode.mc.moblimit;

import java.util.Calendar;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;
import com.worldcretornica.plotme.PlotMe;

public class Main extends JavaPlugin implements Listener{
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		
		Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long delay = (c.getTimeInMillis()-System.currentTimeMillis());
        
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
		    @Override
		    public void run() {
		    	for (World w : Bukkit.getWorlds()){
		    		for (Entity e : w.getEntities()){
		    			if (e.getType() == EntityType.PLAYER) continue;
		    			e.remove();
		    		}
		    	}
		    	
		    	Set<String> keys = getConfig().getKeys(false);
		    	for (String s : keys){
		    		getConfig().set(s, null);
		    	}
		    	saveConfig();
		    }
		}, (delay / 1000) * 20, 1728000);
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem() != null && e.getItem().getType() == Material.MONSTER_EGG){
			Block b = e.getClickedBlock();
			Player p = e.getPlayer();
			boolean canbuild = PlotMe.cPerms(p, "plotme.admin.buildanywhere");
			String id = PlotManager.getPlotId(b.getLocation());
			
			if(id.equalsIgnoreCase(""))
			{
				if(!canbuild)
				{
					p.sendMessage(PlotMe.caption("ErrCannotBuild"));
					e.setCancelled(true);
					return;
				}
			}
			else
			{
				Plot plot = PlotManager.getMap(p).plots.get(id);


				if (plot == null)
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						e.setCancelled(true);
						return;
					}
				}
				else if(!plot.isAllowed(p.getUniqueId()))
				{
					if(!canbuild)
					{
						p.sendMessage(PlotMe.caption("ErrCannotBuild"));
						e.setCancelled(true);
						return;
					}
				}
				else
				{
					plot.resetExpire(PlotManager.getMap(b).DaysToExpiration);
				}
			}
			
			
			if (!getConfig().getKeys(false).contains(e.getPlayer().getUniqueId().toString())){
				getConfig().set(e.getPlayer().getUniqueId().toString(), 1);
				e.getPlayer().sendMessage(ChatColor.GOLD + "Warning: " + ChatColor.YELLOW + "You can only spawn 5 monsters or animals a day");
			}
			else {
				int value = getConfig().getInt(e.getPlayer().getUniqueId().toString()) + 1;
				if (value > 5){
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "You can't spawn more than 5 monsters or animals a day");
					return;
				}
				getConfig().set(e.getPlayer().getUniqueId().toString(), value);
			}
			saveConfig();			
		}
	}
}
