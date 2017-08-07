package com.barancode.mc.perks;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0 && cmd.getName().equalsIgnoreCase("fly")){
			boolean wasflying = ((Player)sender).getAllowFlight();
			((Player)sender).setAllowFlight(!wasflying);
			if (!wasflying) sender.sendMessage(ChatColor.BLUE + "You can now fly!");
			else{
				sender.sendMessage(ChatColor.BLUE + "You have disabled flying");
				((Player)sender).setFlying(false);
			}
			return true;
		} else if (args.length == 2 && cmd.getName().equalsIgnoreCase("speed")){
			if (Float.parseFloat(args[1]) < 0 || Float.parseFloat(args[1]) > 10){
				sender.sendMessage(ChatColor.RED + "The number needs to be from 0 to 10");
				return true;
			}
			if (args[0].equalsIgnoreCase("walk")){
				((Player)sender).setWalkSpeed(Float.parseFloat(args[1]) / 10);
				sender.sendMessage(ChatColor.BLUE + "Your walking speed is now " + args[1]);
				return true;
			} else if (args[0].equalsIgnoreCase("fly")){
				((Player)sender).setFlySpeed(Float.parseFloat(args[1]) / 10);
				sender.sendMessage(ChatColor.BLUE + "Your flying speed is now " + args[1]);
				return true;
			}
		} else if (args.length == 0 && cmd.getName().equalsIgnoreCase("feed")){
			((Player)sender).setFoodLevel(20);
			sender.sendMessage(ChatColor.BLUE + "Your food level is now full");
			return true;
		} else if (args.length == 0 && cmd.getName().equalsIgnoreCase("repair")){
			if (((Player)sender).getItemInHand() == null || ((Player)sender).getItemInHand().getType() == Material.AIR){
				sender.sendMessage(ChatColor.RED + "Please hold the item you want repaired, in your hand");
				return true;
			}
			ItemStack item = ((Player)sender).getItemInHand();
			item.setDurability((short)0);
			((Player)sender).updateInventory();
			sender.sendMessage(ChatColor.BLUE + "Your " + item.getType().toString() + " has been repaired");
			return true;
		}
		return false;
	}
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		/*if (e.getPlayer().hasPermission("bcmc.hearts.40")){
			e.getPlayer().setMaxHealth(40);
		} else if (e.getPlayer().hasPermission("bcmc.hearts.32")){
			e.getPlayer().setMaxHealth(32);
		} else if (e.getPlayer().hasPermission("bcmc.hearts.28")){
			e.getPlayer().setMaxHealth(28);
		} else if (e.getPlayer().hasPermission("bcmc.hearts.24")){
			e.getPlayer().setMaxHealth(24);
		} else {
			e.getPlayer().setMaxHealth(20);
		}*/
		e.getPlayer().setMaxHealth(20);
		
		if (!e.getPlayer().hasPermission("bcmc.speed")){
			e.getPlayer().setWalkSpeed(0.2F);
			e.getPlayer().setFlySpeed(0.1F);
		}
		if (!e.getPlayer().hasPermission("bcmc.fly") && e.getPlayer().getGameMode() != GameMode.CREATIVE){
			e.getPlayer().setAllowFlight(false);
			e.getPlayer().setFlying(false);
		}
	}
}
