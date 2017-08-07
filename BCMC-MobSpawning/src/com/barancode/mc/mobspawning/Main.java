package com.barancode.mc.mobspawning;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
	}
	@EventHandler
	public void onSpawn(CreatureSpawnEvent e){
		if (e.getSpawnReason() != SpawnReason.SPAWNER_EGG) e.setCancelled(true);
		if (e.getEntityType() == EntityType.WITHER) e.setCancelled(true);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		sender.sendMessage("Cleared");
		for (Entity e : ((Player)sender).getWorld().getEntities()){
			if (e.getType() == EntityType.PLAYER) continue;
			e.remove();
		}
		return true;
	}
}
