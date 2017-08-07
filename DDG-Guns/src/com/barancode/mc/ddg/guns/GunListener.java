package com.barancode.mc.ddg.guns;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class GunListener implements Listener{
	  Main plugin;

	  public GunListener(Main plugin)
	  {
	    this.plugin = plugin;
	  }
	  
	  @EventHandler
	  public void preLoginEvent(PlayerLoginEvent e){
		  if (plugin.running){
			  e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			  e.setKickMessage(plugin.utils.replace(plugin.getConfig().getString("game-running")));
		  }
	  }
	  
	  @EventHandler
	  public void joinEvent(PlayerJoinEvent e){
		  plugin.arenamanager.teleportToJoin(e.getPlayer());
		  plugin.menu.open(e.getPlayer());
	  }
}
