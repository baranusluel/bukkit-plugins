package com.barancode.itemsandvisibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class Visibility {
	Main plugin;
	
	public Visibility(Main plugin){
		this.plugin = plugin;
	}
	
	public void hidePlayers(Player p){
        for(Player ps : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(ps);
        }
        p.setMetadata("hiding", new FixedMetadataValue(plugin, true));
	}
	public void showPlayers(Player p){
        for(Player ps : Bukkit.getOnlinePlayers()) {
            p.showPlayer(ps);
        }
        if (p.hasMetadata("hiding")) p.removeMetadata("hiding", plugin);
	}
}
