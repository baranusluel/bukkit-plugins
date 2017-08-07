package com.barancode.mc.spawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin implements Listener{
	BukkitScheduler scheduler = null;
	Location loc = null;
	boolean force = true;
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		scheduler = Bukkit.getServer().getScheduler();
		saveDefaultConfig();
		
		force = getConfig().getBoolean("force");
		
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
                World w = Bukkit.getWorld(getConfig().getString("world"));
                Location temploc = w.getSpawnLocation();
                temploc.setYaw((float)getConfig().getDouble("yaw"));
                temploc.setPitch((float)getConfig().getDouble("pitch"));
                loc = temploc;
            }
        }, 0L);
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent e){
		if (!force) return;
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
  	          e.getPlayer().teleport(loc);
            }
        }, 1L);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("setserverspawn")){
			Location loc = ((Player)sender).getLocation();
			World world = ((Player)sender).getWorld();
			world.setSpawnLocation((int)loc.getX(), (int)loc.getY(), (int)loc.getZ());
			world.save();
			getConfig().set("world", world.getName());
			getConfig().set("pitch", loc.getPitch());
			getConfig().set("yaw", loc.getYaw());
			saveConfig();
			this.loc = loc;
			sender.sendMessage("Set server spawnpoint");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("spawn")){
			((Player)sender).teleport(loc);
			sender.sendMessage(ChatColor.GOLD + "You have been teleported to the spawn of this server");
			return true;
		}
		return false;
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onRespawn(PlayerRespawnEvent e){
		if (!force) return;
		e.setRespawnLocation(loc);
	}
}
