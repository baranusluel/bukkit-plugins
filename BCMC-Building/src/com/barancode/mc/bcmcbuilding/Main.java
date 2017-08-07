package com.barancode.mc.bcmcbuilding;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
	}
	@EventHandler
	public void onMelt(BlockFadeEvent e){
		e.setCancelled(true);
	}
	@EventHandler
	public void onForm(BlockFormEvent e){
		if (e.getNewState().getType() == Material.ICE) e.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e){
		if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			Location l = e.getClickedBlock().getLocation();
			if (l.getWorld().getName().equalsIgnoreCase("plotworld2") && l.getX() <= 106 && l.getX() >= 3 && l.getZ() <= 106 && l.getZ() >= 3){
				if (!e.getPlayer().hasPermission("bcmc.creative.buildatspawn")) e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onLeaf(LeavesDecayEvent e){
		e.setCancelled(true);
	}
}
