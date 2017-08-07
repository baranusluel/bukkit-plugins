package com.barancode.mc.bloodlines.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.barancode.mc.bloodlines.Main;
import com.barancode.mc.bloodlines.items.ClassSelector;
import com.barancode.mc.bloodlines.managers.PlayerManager;

public class PlayerEvents implements Listener{
	
	Main plugin;
	
	public PlayerEvents(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void playerJoin(final PlayerJoinEvent e){
        Main.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run(){
        		PlayerManager.tpWorldSpawn(e.getPlayer());
        		e.getPlayer().getInventory().setItem(0, ClassSelector.item.clone());
            }
        }, 1L);
	}
}
