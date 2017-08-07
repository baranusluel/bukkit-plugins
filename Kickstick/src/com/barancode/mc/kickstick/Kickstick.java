package com.barancode.mc.kickstick;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Kickstick extends JavaPlugin implements Listener{
	
	  Player player;
	  Player clicked;
	  
	  PlayerInventory inventory;
      ItemStack itemstack = new ItemStack(Material.STICK, 1);
      ItemStack itemstack2 = new ItemStack(Material.BLAZE_ROD, 1);
      ItemMeta meta = itemstack.getItemMeta();
      ItemMeta meta2 = itemstack2.getItemMeta();
      List<String> list = new LinkedList<String>();
      List<String> list2 = new LinkedList<String>();
      
      String kickedmessage;
      String kickermessage;
      String bannedmessage;
      String bannermessage;
    
      
      public void onEnable(){
    	  saveDefaultConfig();
    	  getServer().getPluginManager().registerEvents(this, this);
    	  
    	  //Adding the lores to a list
	      list.add(ChatColor.GREEN + "Click players with this stick");
	      list.add(ChatColor.GREEN + "to kick them from the server");
	      //Setting the name of the item meta
	      meta.setDisplayName("Kickstick");
	      //Setting the list as the lore of the item meta
	      meta.setLore(list);
	      //Setting the item meta of the item
	      itemstack.setItemMeta(meta);
	      
    	  //Adding the lores to a list
	      list2.add(ChatColor.GREEN + "Click players with this stick");
	      list2.add(ChatColor.GREEN + "to ban them from the server");
	      //Setting the name of the item meta
	      meta2.setDisplayName("Banstick");
	      //Setting the list as the lore of the item meta
	      meta2.setLore(list2);
	      //Setting the item meta of the item
	      itemstack2.setItemMeta(meta2);
	      
          try {
        	  Metrics metrics = new Metrics(this);
	          metrics.start();
	      } catch (IOException e) {
	    	  getLogger().severe("Metrics for Kickstick are not working!");
	      }
      }
	
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
		  if (cmd.getName().equalsIgnoreCase("kickstick")){
			  if (args.length == 1 && args[0].equalsIgnoreCase("reload")){
				  if (sender.hasPermission("kickstick.reload")){
					  reloadConfig();
					  sender.sendMessage(ChatColor.GREEN + "Reloaded Kickstick config");
					  return true;
				  } else {
					  sender.sendMessage(ChatColor.RED + "You don't have the permission kickstick.reload");
					  return true;
				  }
			  }
			  else if (args.length == 0){
				  if (sender.hasPermission("kickstick.get")){
					  //Making sure it's a player so that no errors occur
					  if (sender instanceof Player) {
				          player = (Player) sender;
				      } else {
				          sender.sendMessage(ChatColor.RED + "You must be a player!");
				          return true;
				      }
					  
					  //Get the inventory
					  inventory = player.getInventory();
					  if (!inventory.contains(itemstack)){
						  //Add the item to the inventory
					      inventory.addItem(itemstack);
					      //Tell the player
						  player.sendMessage(ChatColor.GREEN + "Giving the kicking stick");
					  } else player.sendMessage(ChatColor.RED + "You already have a kickstick!");
					  return true;
				  }
				  else {
					  sender.sendMessage(ChatColor.RED + "You don't have the permission kickstick.get");
					  return true;
				  }
			  }
		  } else if (cmd.getName().equalsIgnoreCase("banstick")){
			  if (args.length == 0){
				  if (sender.hasPermission("kickstick.getbanstick")){
					  //Making sure it's a player so that no errors occur
					  if (sender instanceof Player) {
				          player = (Player) sender;
				      } else {
				          sender.sendMessage(ChatColor.RED + "You must be a player!");
				          return true;
				      }
					  
					  //Get the inventory
					  inventory = player.getInventory();
					  if (!inventory.contains(itemstack2)){
						  //Add the item to the inventory
					      inventory.addItem(itemstack2);
					      //Tell the player
						  player.sendMessage(ChatColor.GREEN + "Giving the banning stick");
					  } else player.sendMessage(ChatColor.RED + "You already have a banstick!");
					  return true;
				  }
				  else {
					  sender.sendMessage(ChatColor.RED + "You don't have the permission kickstick.getbanstick");
					  return true;
				  }
			  }
		  }
		  return false;
	  }
	  
	  @EventHandler
	  public void rightclick(PlayerInteractEntityEvent event){
		  //Checking if the player is holding the kickstick, if the player has permission, and if the targeted entity is a player
		  if (player.getItemInHand().equals(itemstack) && event.getRightClicked().getType().equals(EntityType.PLAYER)){
			  if (event.getPlayer().hasPermission("kickstick.use")){
				  //Get the clicked player
				  clicked = (Player) event.getRightClicked();
				  
				  kickedmessage = replaceColors(getConfig().getString("kickedmessage"));
				  
				  kickedmessage = kickedmessage.replaceAll("KICKER", player.getName());
				  kickedmessage = kickedmessage.replaceAll("KICKED", clicked.getName());
				  
				  
				  kickermessage = replaceColors(getConfig().getString("kickermessage"));
				  
				  kickermessage = kickermessage.replaceAll("KICKER", player.getName());
				  kickermessage = kickermessage.replaceAll("KICKED", clicked.getName());
				  
				  
				  //Kick player
				  clicked.kickPlayer(kickedmessage);
				  //Tell the kicker
				  player.sendMessage(kickermessage);
				  getLogger().info(player.getName() + " has kicked " + clicked.getName() + " from the server with a kickstick");
			  } else {
				  event.getPlayer().sendMessage(ChatColor.RED + "You don't have the permission kickstick.use");
			  }
		  } else if (player.getItemInHand().equals(itemstack2) && event.getRightClicked().getType().equals(EntityType.PLAYER)){
			  if (event.getPlayer().hasPermission("kickstick.usebanstick")){
				  //Get the clicked player
				  clicked = (Player) event.getRightClicked();
				  
				  bannedmessage = replaceColors(getConfig().getString("bannedmessage"));
				  
				  bannedmessage = bannedmessage.replaceAll("BANNER", player.getName());
				  bannedmessage = bannedmessage.replaceAll("BANNED", clicked.getName());
				  
				  
				  bannermessage = replaceColors(getConfig().getString("bannermessage"));
				  
				  bannermessage = bannermessage.replaceAll("BANNER", player.getName());
				  bannermessage = bannermessage.replaceAll("BANNED", clicked.getName());
				  
				  
				  //Kick player
				  clicked.setBanned(true);
				  clicked.kickPlayer(bannedmessage);
				  //Tell the kicker
				  player.sendMessage(bannermessage);
				  getLogger().info(player.getName() + " has banned " + clicked.getName() + " from the server with a banstick");
			  } else {
				  event.getPlayer().sendMessage(ChatColor.RED + "You don't have the permission kickstick.usebanstick");
			  }
		  }
	  }
	  
	  //Lowest priority so that it gets run first, and it can do what it does before some plugin disables PvP
	  @EventHandler(priority = EventPriority.LOWEST)
	  public void leftclick(EntityDamageByEntityEvent event){
		  //Check if it was a player that did the damage
		  if (event.getDamager().getType().equals(EntityType.PLAYER)) {
			  //Get the entity, as a player
			  player = (Player) event.getDamager();
			  //Checking if the player is holding the kickstick, if the player has permission, and if the targeted entity is a player
			  if (player.getItemInHand().equals(itemstack) && event.getEntity().getType().equals(EntityType.PLAYER)){
				  if (player.hasPermission("kickstick.use")){
					  //Get the clicked player
					  clicked = (Player) event.getEntity();
					  
					  kickedmessage = replaceColors(getConfig().getString("kickedmessage"));
					  
					  kickedmessage = kickedmessage.replaceAll("KICKER", player.getName());
					  kickedmessage = kickedmessage.replaceAll("KICKED", clicked.getName());
					  
					  
					  kickermessage = replaceColors(getConfig().getString("kickermessage"));
					  
					  kickermessage = kickermessage.replaceAll("KICKER", player.getName());
					  kickermessage = kickermessage.replaceAll("KICKED", clicked.getName());
					  
					  
					  //Kick player
					  clicked.kickPlayer(kickedmessage);
					  //Tell the kicker
					  player.sendMessage(kickermessage);
					  getLogger().info(player.getName() + " has kicked " + clicked.getName() + " from the server with a kickstick");
					  
				  } else player.sendMessage(ChatColor.RED + "You don't have the permission kickstick.use");
			  } else if (player.getItemInHand().equals(itemstack2) && event.getEntity().getType().equals(EntityType.PLAYER)){
				  if (player.hasPermission("kickstick.usebanstick")){
					  //Get the clicked player
					  clicked = (Player) event.getEntity();
					  
					  bannedmessage = replaceColors(getConfig().getString("bannedmessage"));
					  
					  bannedmessage = bannedmessage.replaceAll("BANNER", player.getName());
					  bannedmessage = bannedmessage.replaceAll("BANNED", clicked.getName());
					  
					  
					  bannermessage = replaceColors(getConfig().getString("bannermessage"));
					  
					  bannermessage = bannermessage.replaceAll("BANNER", player.getName());
					  bannermessage = bannermessage.replaceAll("BANNED", clicked.getName());
					  
					  
					  //Kick player
					  clicked.setBanned(true);
					  clicked.kickPlayer(bannedmessage);
					  //Tell the kicker
					  player.sendMessage(bannermessage);
					  getLogger().info(player.getName() + " has banned " + clicked.getName() + " from the server with a banstick");
				  } else {
					  player.sendMessage(ChatColor.RED + "You don't have the permission kickstick.usebanstick");
				  }
			  }
		  }
	  }
	  
	  
	  public String replaceColors(String message){
		    message = message.replaceAll("&0", ChatColor.BLACK + "");
		    message = message.replaceAll("&1", ChatColor.DARK_BLUE + "");
		    message = message.replaceAll("&2", ChatColor.DARK_GREEN + "");
		    message = message.replaceAll("&3", ChatColor.DARK_AQUA + "");
		    message = message.replaceAll("&4", ChatColor.DARK_RED + "");
		    message = message.replaceAll("&5", ChatColor.DARK_PURPLE + "");
		    message = message.replaceAll("&6", ChatColor.GOLD + "");
		    message = message.replaceAll("&7", ChatColor.GRAY + "");
		    message = message.replaceAll("&8", ChatColor.DARK_GRAY + "");
		    message = message.replaceAll("&9", ChatColor.BLUE + "");
		    message = message.replaceAll("(?i)&a", ChatColor.GREEN + "");
		    message = message.replaceAll("(?i)&b", ChatColor.AQUA + "");
		    message = message.replaceAll("(?i)&c", ChatColor.RED + "");
		    message = message.replaceAll("(?i)&d", ChatColor.LIGHT_PURPLE + "");
		    message = message.replaceAll("(?i)&e", ChatColor.YELLOW + "");
		    message = message.replaceAll("(?i)&f", ChatColor.WHITE + "");
		    message = message.replaceAll("(?i)&l", ChatColor.BOLD + "");
		    message = message.replaceAll("(?i)&o", ChatColor.ITALIC + "");
		    message = message.replaceAll("(?i)&m", ChatColor.STRIKETHROUGH + "");
		    message = message.replaceAll("(?i)&n", ChatColor.UNDERLINE + "");
		    message = message.replaceAll("(?i)&k", ChatColor.MAGIC + "");
		    message = message.replaceAll("(?i)&r", ChatColor.RESET + "");
		    return message;
	  }
}
