package com.barancode.mc.antiredstoneclock;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin implements Listener{
	ConcurrentHashMap<Location, Tracker> hashmap = new ConcurrentHashMap<Location, Tracker>();
	BukkitScheduler scheduler;
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		scheduler = Bukkit.getScheduler();
	}
	public void onDisable(){
		scheduler.cancelAllTasks();
	}
	@EventHandler
	public void redstoneEvent(final BlockRedstoneEvent e){
		getLogger().info(e.getBlock().getLocation() + "");
		if (e.getBlock().getType() != Material.REDSTONE_WIRE) return;
		if (!hashmap.containsKey(e.getBlock().getLocation())) hashmap.put(e.getBlock().getLocation(), new Tracker());
		else {
			Tracker tracker = hashmap.get(e.getBlock().getLocation());
			long time = System.currentTimeMillis();
			if (time - tracker.last > 2000){
				hashmap.put(e.getBlock().getLocation(), new Tracker());
				return;
			}
			if (tracker.amount < 6){
				tracker.updateTime();
				tracker.increaseAmount();
			} else {
				hashmap.remove(e.getBlock().getLocation());
				/*Block block = e.getBlock();
				block.getState().setType(Material.SIGN);
				Sign sign = (Sign)block.getState();
				sign.setLine(0, "You are not");
				sign.setLine(1, "allowed to make");
				sign.setLine(2, "redstone");
				sign.setLine(3, "clocks!");*/
				e.setNewCurrent(e.getOldCurrent());
				e.getBlock().setType(Material.AIR);
			}
		}
		getLogger().info("run");
	}
}
