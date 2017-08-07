package com.barancode.mc.bcmcspy;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Events implements Listener{
	
	Main plugin;
	
	public Events(Main plugin) {
	    ProxyServer.getInstance().getPluginManager().registerListener(plugin, this);
	    this.plugin = plugin;
	}
	
	@EventHandler
	public void event(ChatEvent e){
		if (e.isCommand()){
			try {
				plugin.getProxy().getPlayer("BaranCODE").sendMessage(ChatColor.GRAY + "" + e.getSender() + " did: " + e.getMessage());
				plugin.getProxy().getPlayer("brbtus").sendMessage(ChatColor.GRAY + "" + e.getSender() + " did: " + e.getMessage());
				plugin.getProxy().getPlayer("Stevenking2e").sendMessage(ChatColor.GRAY + "" + e.getSender() + " did: " + e.getMessage());
			} catch (Exception exception) {
				
			}
		}
	}
}
