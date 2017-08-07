package com.barancode.mc.custom;


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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class Main extends JavaPlugin implements Listener{
	
	int count = 1;
	
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@EventHandler
	public void commandPreprocess(PlayerCommandPreprocessEvent event){
		String s = event.getMessage();
		
		if (s.startsWith("/warp")){
			for (PotionEffect effect : event.getPlayer().getActivePotionEffects()){
				event.getPlayer().removePotionEffect(effect.getType());
				event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
			}
		}
	}
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void consoleCommand(ServerCommandEvent event){
		String s = event.getCommand();
		if (s.startsWith("say")) event.setCommand("");
	}
	
	
    @EventHandler(priority=EventPriority.NORMAL)
    public void onVotifierEvent(VotifierEvent event) {
    	Vote vote = event.getVote();
    	if (getConfig().getInt("votes." + vote.getUsername()) < 3){
    		for (Player p : Bukkit.getOnlinePlayers()){
    			p.sendMessage(ChatColor.GREEN + vote.getUsername() + " voted for our server and got $100, 1 diamond, and awesome potions!");
    		}
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " 373 1 8290");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " 373 1 8289");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " 373 2 8229");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " diamond 1");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + vote.getUsername() + " 100");
	        int i = getConfig().getInt("votes." + vote.getUsername().toLowerCase());
	        i++;
	        getConfig().set("votes." + vote.getUsername().toLowerCase(), i);
    	} else if (getConfig().getInt("votes." + vote.getUsername()) == 3){
    		for (Player p : Bukkit.getOnlinePlayers()){
    			p.sendMessage(ChatColor.GREEN + vote.getUsername() + " voted for our server and got " + ChatColor.BOLD + "access to The Mysterious House 2" + ChatColor.GREEN + ", as well as $100, 1 diamond, and awesome potions!");
    		}
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " 373 1 8290");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " 373 1 8289");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " 373 2 8229");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " diamond 1");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + vote.getUsername() + " 100");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + vote.getUsername() + " add bungeesuite.warps.warp.mh2");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + vote.getUsername() + " add bungeesuite.warps.warp.MysteriousHouse2");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + vote.getUsername() + " add bungeesuite.portals.portal.mh2");
	        int i = getConfig().getInt("votes." + vote.getUsername().toLowerCase());
	        i++;
	        getConfig().set("votes." + vote.getUsername().toLowerCase(), i);
    	} else {
    		for (Player p : Bukkit.getOnlinePlayers()){
    			p.sendMessage(ChatColor.GREEN + vote.getUsername() + " voted for our server and got $100, 1 diamond, and awesome potions!");
    		}
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " 373 1 8290");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " 373 1 8289");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " 373 2 8229");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + vote.getUsername() + " diamond 1");
	        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + vote.getUsername() + " 100");
	        int i = getConfig().getInt("votes." + vote.getUsername().toLowerCase());
	        i++;
	        getConfig().set("votes." + vote.getUsername().toLowerCase(), i);
    	}
        saveConfig();
    }
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("apply")){
			if (args.length == 0){
				sender.sendMessage(ChatColor.GOLD + "You can apply for moderator at: " + ChatColor.DARK_GRAY + "http://mcforums.barancode.com/viewtopic.php?f=46&t=10");
				sender.sendMessage(ChatColor.GOLD + "You can apply for the youtuber rank at: " + ChatColor.DARK_GRAY + "http://mcforums.barancode.com/viewtopic.php?f=46&t=35");
			} else if (args.length == 1){
				if (((Player)sender).isOp()){
					Bukkit.getPlayer(args[0]).sendMessage(ChatColor.GOLD + "You can apply for moderator at: " + ChatColor.DARK_GRAY + "http://mcforums.barancode.com/viewtopic.php?f=46&t=10");
					Bukkit.getPlayer(args[0]).sendMessage(ChatColor.GOLD + "You can apply for the youtuber rank at: " + ChatColor.DARK_GRAY + "http://mcforums.barancode.com/viewtopic.php?f=46&t=35");
					sender.sendMessage("You have sent that person the link");
				}
				else sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
			}
		} else if (cmd.getName().equalsIgnoreCase("mh2")){
			if (sender instanceof Player){
				Player p = Bukkit.getPlayer(args[0]);
				if (getConfig().getInt("votes." + args[0].toLowerCase()) >= 4){
					Location loc = Bukkit.getWorld("MysteriousHouse2").getSpawnLocation();
					p.teleport(loc);
				} else {
					p.sendMessage(ChatColor.RED + "You haven't voted 4 times!");
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("setcheckpoint")){
			Player player = Bukkit.getPlayer(args[0]);
			if (player.getWorld().getName().equalsIgnoreCase("MysteriousHouse") || player.getWorld().getName().equalsIgnoreCase("MysteriousHouse2") || player.getWorld().getName().equalsIgnoreCase("minigames")){
				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("back")){
						if (getConfig().getBoolean("spawns." + player.getName() + "." + player.getWorld().getName() + ".exists")){
					          World w = player.getWorld();
					          double x = getConfig().getDouble("spawns." + player.getName() + "." + player.getWorld().getName() + ".x");
					          double y = getConfig().getDouble("spawns." + player.getName() + "." + player.getWorld().getName() + ".y");
					          double z = getConfig().getDouble("spawns." + player.getName() + "." + player.getWorld().getName() + ".z");
					          float yaw = (float)getConfig().getDouble("spawns." + player.getName() + "." + player.getWorld().getName() + ".yaw");
					          float pitch = (float)getConfig().getDouble("spawns." + player.getName() + "." + player.getWorld().getName() + ".pitch");
					          Location loc = new Location(w, x, y, z, yaw, pitch);
					          player.teleport(loc);
				        	  player.sendMessage(ChatColor.GREEN + "You have been teleported to your last checkpoint");
				        	  return true;
						} else {
							player.sendMessage(ChatColor.RED + "You don't have a checkpoint");
							return true;
						}
					} else if (args[1].equalsIgnoreCase("first")){
						getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".x", player.getLocation().getX());
						getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".y", player.getLocation().getY());
						getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".z", player.getLocation().getZ());
						getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".yaw", player.getLocation().getYaw());
						getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".pitch", player.getLocation().getPitch());
						getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".exists", true);
						getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".count", 0);
						getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".totalcount", 0);
						return true;
					}
				}
				getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".x", player.getLocation().getX());
				getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".y", player.getLocation().getY());
				getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".z", player.getLocation().getZ());
				getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".yaw", player.getLocation().getYaw());
				getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".pitch", player.getLocation().getPitch());
				getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".exists", true);
				getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".count", 0);
				player.sendMessage(ChatColor.GREEN + "You have set your spawnpoint");
				saveConfig();
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("bcmcreload")){
			reloadConfig();
			sender.sendMessage("Reloaded");
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void death(PlayerDeathEvent event){
		if (event.getEntity().getWorld().getName().equalsIgnoreCase("MysteriousHouse") || event.getEntity().getWorld().getName().equalsIgnoreCase("MysteriousHouse2") || event.getEntity().getWorld().getName().equalsIgnoreCase("minigames")){
			event.setDeathMessage(null);
			event.setDroppedExp(0);
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void respawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if (player.getWorld().getName().equalsIgnoreCase("MysteriousHouse") || player.getWorld().getName().equalsIgnoreCase("MysteriousHouse2") || player.getWorld().getName().equalsIgnoreCase("minigames")){
			if (getConfig().getBoolean("spawns." + player.getName() + "." + player.getWorld().getName() + ".exists")){
		          World w = player.getWorld();
		          double x = getConfig().getDouble("spawns." + player.getName() + "." + player.getWorld().getName() + ".x");
		          double y = getConfig().getDouble("spawns." + player.getName() + "." + player.getWorld().getName() + ".y");
		          double z = getConfig().getDouble("spawns." + player.getName() + "." + player.getWorld().getName() + ".z");
		          float yaw = (float)getConfig().getDouble("spawns." + player.getName() + "." + player.getWorld().getName() + ".yaw");
		          float pitch = (float)getConfig().getDouble("spawns." + player.getName() + "." + player.getWorld().getName() + ".pitch");
		          Location loc = new Location(w, x, y, z, yaw, pitch);
		          event.setRespawnLocation(loc);
		          int count = getConfig().getInt("spawns." + player.getName() + "." + player.getWorld().getName() + ".count");
		          int totalcount = getConfig().getInt("spawns." + player.getName() + "." + player.getWorld().getName() + ".totalcount");
		          count++;
		          totalcount++;
		          getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".count", count);
		          getConfig().set("spawns." + player.getName() + "." + player.getWorld().getName() + ".totalcount", totalcount);
		          saveConfig();
		          if (player.getWorld().getName().equalsIgnoreCase("MysteriousHouse") || player.getWorld().getName().equalsIgnoreCase("MysteriousHouse2")){
			          if (count != 1){
			        	  player.sendMessage(ChatColor.GREEN + "Teleporting you back to the last checkpoint.");
			        	  player.sendMessage(ChatColor.DARK_GREEN + "- You have died " + count + " times in this level.");
			        	  player.sendMessage(ChatColor.DARK_GREEN + "- You have died " + totalcount + " times in total.");
			          } else {
			        	  player.sendMessage(ChatColor.GREEN + "Teleporting you back to the last checkpoint.");
			        	  player.sendMessage(ChatColor.DARK_GREEN + "- You have only died 1 time in this level.");
			        	  player.sendMessage(ChatColor.DARK_GREEN + "- You have died " + totalcount + " times in total.");
			          }
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
