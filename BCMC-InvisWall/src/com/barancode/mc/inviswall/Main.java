package com.barancode.mc.inviswall;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	String name = "";
	
	Location first;
	
	List<Wall> walls = new LinkedList<Wall>();
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		
		for (String s : getConfig().getConfigurationSection("walls").getKeys(false)){
			String world = getConfig().getString("walls." + s + ".world");
			double minx = getConfig().getDouble("walls." + s + ".minx");
			double miny = getConfig().getDouble("walls." + s + ".miny");
			double minz = getConfig().getDouble("walls." + s + ".minz");
			double maxx = getConfig().getDouble("walls." + s + ".maxx");
			double maxy = getConfig().getDouble("walls." + s + ".maxy");
			double maxz = getConfig().getDouble("walls." + s + ".maxz");
			walls.add(new Wall(minx, miny, minz, maxx, maxy, maxz, world, s));
		}
	}
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		for (Wall p : walls){
			if (e.getTo().getWorld().getName().equals(p.world) && e.getTo().getBlockX() <= p.maxx && e.getTo().getBlockX() >= p.minx && e.getTo().getBlockY() <= p.maxy && e.getTo().getBlockY() >= p.miny && e.getTo().getBlockZ() <= p.maxz && e.getTo().getBlockZ() >= p.minz){
				if (p.name.equals("mg1")){
					Location loc = e.getFrom();
					loc.setZ(loc.getZ() + 1);
					e.getPlayer().teleport(loc);
				} else if (p.name.equals("mg2")){
					Location loc = e.getFrom();
					loc.setZ(loc.getZ() - 1);
					e.getPlayer().teleport(loc);
				} else if (p.name.equals("mg3")){
					Location loc = e.getFrom();
					loc.setX(loc.getX() - 1);
					e.getPlayer().teleport(loc);
				} else {
					e.setCancelled(true);
				}
				return;
			}
		}
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if (e.getPlayer().getItemInHand().getType() == Material.BRICK && e.getPlayer().hasPermission("bcmc.setportal") && !name.equals("")){
			if (e.getAction() == Action.LEFT_CLICK_BLOCK){
				first = e.getClickedBlock().getLocation();
				e.getPlayer().sendMessage("First position set");
			} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
				Location second = e.getClickedBlock().getLocation();
				double maxx;
				double maxy;
				double maxz;
				double minx;
				double miny;
				double minz;
				if (first.getX() > second.getX()){
					maxx = first.getX();
					minx = second.getX();
				} else {
					maxx = second.getX();
					minx = first.getX();
				}
				if (first.getY() > second.getY()){
					maxy = first.getY();
					miny = second.getY();
				} else {
					maxy = second.getY();
					miny = first.getY();
				}
				if (first.getZ() > second.getZ()){
					maxz = first.getZ();
					minz = second.getZ();
				} else {
					maxz = second.getZ();
					minz = first.getZ();
				}
				
				getConfig().set("walls." + name + ".world", second.getWorld().getName());
				getConfig().set("walls." + name + ".minx", minx);
				getConfig().set("walls." + name + ".miny", miny);
				getConfig().set("walls." + name + ".minz", minz);
				getConfig().set("walls." + name + ".maxx", maxx);
				getConfig().set("walls." + name + ".maxy", maxy);
				getConfig().set("walls." + name + ".maxz", maxz);
				saveConfig();
				
				walls.add(new Wall(minx, miny, minz, maxx, maxy, maxz, second.getWorld().getName(), name));
				
				name = "";
				first = null;
				
				e.getPlayer().sendMessage("The invisible wall has been saved");
			}
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 1){
			name = args[0];
			sender.sendMessage("Wall creation process started");
			return true;
		}
		return false;
	}

}
