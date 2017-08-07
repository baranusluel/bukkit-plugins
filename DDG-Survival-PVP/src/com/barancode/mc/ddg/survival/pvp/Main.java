package com.barancode.mc.ddg.survival.pvp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	VillagerPlayerList vgl = new VillagerPlayerList(this);
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
	}
	
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
		  if (cmd.getName().equalsIgnoreCase("spawnvillager")){
			  
		  }
		  return false;
	  }
	
	@EventHandler
	public void entityDamageByEntity(EntityDamageByEntityEvent e){
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player){			
			if (e.getEntity().getWorld().getName().equalsIgnoreCase("pvp")){
				e.setCancelled(false);
			} else if (e.getEntity().getWorld().getName().equalsIgnoreCase("world")){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void blockPlaceEvent(BlockPlaceEvent e){
		if (e.getBlock().getWorld().getName().equalsIgnoreCase("pvp")){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void blockBreakEvent(BlockBreakEvent e){
		if (e.getBlock().getWorld().getName().equalsIgnoreCase("pvp")){
			e.setCancelled(true);
		}
	}		
}
