package com.barancode.mc.portals;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	String name = "";
	String world = "";
	double targetx;
	double targety;
	double targetz;
	String command = "";
	
	Location first;
	
	List<Portal> portals = new LinkedList<Portal>();
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		
		for (String s : getConfig().getConfigurationSection("portals").getKeys(false)){
			String world = getConfig().getString("portals." + s + ".world");
			double minx = getConfig().getDouble("portals." + s + ".minx");
			double miny = getConfig().getDouble("portals." + s + ".miny");
			double minz = getConfig().getDouble("portals." + s + ".minz");
			double maxx = getConfig().getDouble("portals." + s + ".maxx");
			double maxy = getConfig().getDouble("portals." + s + ".maxy");
			double maxz = getConfig().getDouble("portals." + s + ".maxz");
			portals.add(new Portal(minx, miny, minz, maxx, maxy, maxz, world, s));
		}
	}
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (e.getFrom().getBlock().getLocation().distance(e.getTo().getBlock().getLocation()) >= 1){
			for (Portal p : portals){
				if (e.getTo().getWorld().getName().equals(p.world) && e.getTo().getBlockX() <= p.maxx && e.getTo().getBlockX() >= p.minx && e.getTo().getBlockY() <= p.maxy && e.getTo().getBlockY() >= p.miny && e.getTo().getBlockZ() <= p.maxz && e.getTo().getBlockZ() >= p.minz){
					World world = Bukkit.getWorld(getConfig().getString("portals." + p.name + ".target.world"));
					double x = getConfig().getDouble("portals." + p.name + ".target.x");
					double y = getConfig().getDouble("portals." + p.name + ".target.y");
					double z = getConfig().getDouble("portals." + p.name + ".target.z");
					e.getPlayer().teleport(new Location(world, x, y, z));
					String command = getConfig().getString("portals." + p.name + ".command");
					if (command != null && !command.equals("")) Bukkit.dispatchCommand((CommandSender)e.getPlayer(), command);
					e.getPlayer().sendMessage(ChatColor.GOLD + "To teleport back to the spawn of this server, do " + ChatColor.GRAY + "/spawn");
				}
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
				
				getConfig().set("portals." + name + ".target.world", world);
				getConfig().set("portals." + name + ".target.x", targetx);
				getConfig().set("portals." + name + ".target.y", targety);
				getConfig().set("portals." + name + ".target.z", targetz);
				getConfig().set("portals." + name + ".world", second.getWorld().getName());
				getConfig().set("portals." + name + ".minx", minx);
				getConfig().set("portals." + name + ".miny", miny);
				getConfig().set("portals." + name + ".minz", minz);
				getConfig().set("portals." + name + ".maxx", maxx);
				getConfig().set("portals." + name + ".maxy", maxy);
				getConfig().set("portals." + name + ".maxz", maxz);
				getConfig().set("portals." + name + ".command", command);
				saveConfig();
				
				portals.add(new Portal(minx, miny, minz, maxx, maxy, maxz, second.getWorld().getName(), name));
				
				name = "";
				world = "";
				targetx = 0;
				targety = 0;
				targetz = 0;
				first = null;
				command = "";
				
				e.getPlayer().sendMessage("The portal has been saved");
			}
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 5 && cmd.getName().equalsIgnoreCase("setportal")){
			name = args[0];
			world = args[1];
			targetx = Double.parseDouble(args[2]);
			targety = Double.parseDouble(args[3]);
			targetz = Double.parseDouble(args[4]);
			sender.sendMessage("Portal creation process started");
			return true;
		} else if (args.length == 6 && cmd.getName().equalsIgnoreCase("setportal")){
			name = args[0];
			world = args[1];
			targetx = Double.parseDouble(args[2]);
			targety = Double.parseDouble(args[3]);
			targetz = Double.parseDouble(args[4]);
			command = args[5];
			sender.sendMessage("Portal creation process started");
			return true;
		} else if (args.length == 1 && cmd.getName().equalsIgnoreCase("warp")){
			String worldname = getConfig().getString("portals." + args[0] + ".target.world");
			if (worldname == null){
				sender.sendMessage(ChatColor.RED + "That warp doesn't exist");
				return true;
			}
			World world = Bukkit.getWorld(worldname);
			double x = getConfig().getDouble("portals." + args[0] + ".target.x");
			double y = getConfig().getDouble("portals." + args[0] + ".target.y");
			double z = getConfig().getDouble("portals." + args[0] + ".target.z");
			((Player)sender).teleport(new Location(world, x, y, z));
			sender.sendMessage(ChatColor.GOLD + "To teleport back to the spawn of this server, do " + ChatColor.GRAY + "/spawn");
			return true;
		}
		return false;
	}
}
