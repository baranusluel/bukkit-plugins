package com.barancode.mc.weaponsofancientgods;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor{
	
	  public Main plugin;
	  Player player;
	
	  public Commands(Main plugin)
	  {
	    this.plugin = plugin;
	  }
	  
	  @Override
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
		  if (sender instanceof Player) {
	          player = (Player) sender;
	      } else {
	          sender.sendMessage(ChatColor.RED + "You must be a player!");
	          return true;
	      }
		  
		  if (cmd.getName().equalsIgnoreCase("zeus")){
			  player.sendMessage(ChatColor.GOLD + "Summoning the lightning of Zeus");
			  plugin.zeus(player);
			  return true;
		  } else if (cmd.getName().equalsIgnoreCase("poseidon")){
			  player.sendMessage(ChatColor.GOLD + "Summoning the trident of Poseidon");
			  plugin.poseidon(player);
			  return true;
		  } else if (cmd.getName().equalsIgnoreCase("hades")){
			  player.sendMessage(ChatColor.GOLD + "Summoning the staff of Hades");
			  plugin.hades(player);
			  return true;
		  } else if (cmd.getName().equalsIgnoreCase("thor")){
			  player.sendMessage(ChatColor.GOLD + "Summoning the hammer of Thor");
			  plugin.thor(player);
			  return true;
		  } else if (cmd.getName().equalsIgnoreCase("loki")){
			  player.sendMessage(ChatColor.GOLD + "Summoning the sceptre of Loki");
			  plugin.loki(player);
			  return true;
		  } else if (cmd.getName().equalsIgnoreCase("odin")){
			  player.sendMessage(ChatColor.GOLD + "Summoning the spear of Odin");
			  plugin.odin(player);
			  return true;
		  }
		  return false;
	  }

}
