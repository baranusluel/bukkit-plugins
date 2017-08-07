package com.barancode.flyandbarrier;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	String name = "";
	Location first;
	List<Region> regions = new LinkedList<Region>();
	String error = "";
	
	public void onEnable(){
		saveDefaultConfig();
	    getServer().getPluginManager().registerEvents(this, this);
	    error = ChatColor.translateAlternateColorCodes('&', getConfig().getString("error"));
	    
	    if (getConfig().getConfigurationSection("regions") == null) return;
		for (String s : getConfig().getConfigurationSection("regions").getKeys(false)){
			String world = getConfig().getString("regions." + s + ".world");
			double minx = getConfig().getDouble("regions." + s + ".minx");
			double miny = getConfig().getDouble("regions." + s + ".miny");
			double minz = getConfig().getDouble("regions." + s + ".minz");
			double maxx = getConfig().getDouble("regions." + s + ".maxx");
			double maxy = getConfig().getDouble("regions." + s + ".maxy");
			double maxz = getConfig().getDouble("regions." + s + ".maxz");
			regions.add(new Region(minx, miny, minz, maxx, maxy, maxz, world, s));
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if (p.hasPermission("lobby.fly")) p.setAllowFlight(true);
		else {
			p.setAllowFlight(false);
			p.setFlying(false);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (e.getFrom().getBlock().equals(e.getTo().getBlock())) return;
		for (Region p : regions){
			if (e.getTo().getWorld().getName().equals(p.world) && e.getTo().getBlockX() <= p.maxx && e.getTo().getBlockX() >= p.minx && e.getTo().getBlockY() <= p.maxy && e.getTo().getBlockY() >= p.miny && e.getTo().getBlockZ() <= p.maxz && e.getTo().getBlockZ() >= p.minz){
				if (!e.getPlayer().hasPermission("lobby.bypass")){
					e.setCancelled(true);
					e.getPlayer().sendMessage(error);
				}
				return;
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if (e.getPlayer().getItemInHand().getType() == Material.CLAY_BRICK && e.getPlayer().hasPermission("lobby.setregion") && !name.equals("")){
			if (e.getAction() == Action.LEFT_CLICK_BLOCK){
				first = e.getClickedBlock().getLocation();
				e.getPlayer().sendMessage("First position set");
			} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
				if (first == null){
					e.getPlayer().sendMessage(ChatColor.RED + "Set the first position (left-click), first");
				}
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
				
				getConfig().set("regions." + name + ".world", second.getWorld().getName());
				getConfig().set("regions." + name + ".minx", minx);
				getConfig().set("regions." + name + ".miny", miny);
				getConfig().set("regions." + name + ".minz", minz);
				getConfig().set("regions." + name + ".maxx", maxx);
				getConfig().set("regions." + name + ".maxy", maxy);
				getConfig().set("regions." + name + ".maxz", maxz);
				saveConfig();
				
				regions.add(new Region(minx, miny, minz, maxx, maxy, maxz, second.getWorld().getName(), name));
				
				name = "";
				first = null;
				
				e.getPlayer().sendMessage("The second position has been set, and the region has been saved");
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 1){
			name = args[0];
			sender.sendMessage("Region creation process started\nSet the 1st and 2nd positions of the region by left-clicking and then right-clicking with a brick");
			return true;
		}
		return false;
	}
}
