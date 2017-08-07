package com.barancode.mc.bcmcnobuild;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	List<String> building = new LinkedList<String>();
	Log log = new Log();
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		try {
			log.initialize(this);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e){
		if (!building.contains(e.getPlayer().getName())) e.setCancelled(true);
		else {
			e.getPlayer().sendMessage(ChatColor.RED + "You broke a " + e.getBlock().getType().toString());
			try {
				log.write(e.getPlayer().getName() + " (" + e.getPlayer().getUniqueId() + ") broke a " + e.getBlock().getType().toString() + " at: " + e.getBlock().getWorld().getName() + " " + e.getBlock().getLocation().getX() + " " + e.getBlock().getLocation().getY() + " " + e.getBlock().getLocation().getZ());
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent e){
		if (!building.contains(e.getPlayer().getName())) e.setCancelled(true);
		else {
			e.getPlayer().sendMessage(ChatColor.RED + "You placed a " + e.getBlock().getType().toString());
			try {
				log.write(e.getPlayer().getName() + " (" + e.getPlayer().getUniqueId() + ") placed a " + e.getBlock().getType().toString() + " at: " + e.getBlock().getWorld().getName() + " " + e.getBlock().getLocation().getX() + " " + e.getBlock().getLocation().getY() + " " + e.getBlock().getLocation().getZ());
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBurn(BlockBurnEvent e){
		e.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onSpread(BlockSpreadEvent e){
		if (e.getSource().getType() == Material.FIRE){
			e.setCancelled(true);
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onIgnite(BlockIgniteEvent e){
		if (e.getCause() != IgniteCause.FLINT_AND_STEEL) e.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onFoodChange(FoodLevelChangeEvent e){
		if (e.getFoodLevel() < ((Player)e.getEntity()).getFoodLevel()){
			e.setFoodLevel(20);
		}
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		if (building.contains(e.getPlayer().getName())) building.remove(e.getPlayer().getName());
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!building.contains(((Player)sender).getName())){
			building.add(((Player)sender).getName());
			sender.sendMessage(ChatColor.GOLD + "You can now modify/edit worlds on this server.\n" + ChatColor.RED + "BE VERY CAREFUL!");
		} else {
			building.remove(((Player)sender).getName());
			sender.sendMessage(ChatColor.GOLD + "You can no longer build");
		}
		return true;
	}
}
