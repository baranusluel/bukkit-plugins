package com.barancode.mc.jumpingblocks;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener{
	
	List<FallingBlock> entities = new LinkedList<FallingBlock>();
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run(){
            	for (int i = 1; i <= getConfig().getInt("lastid"); i++){
            		int x1 = getConfig().getInt("blocks." + i + ".x");
            		int y1 = getConfig().getInt("blocks." + i + ".y");
            		int z1 = getConfig().getInt("blocks." + i + ".z");
            		String world = getConfig().getString("blocks." + i + ".world");
            		String material = getConfig().getString("blocks." + i + ".type");
            		
            		if (isInteger(material)){
            			int id = Integer.parseInt(material);
            			
            			FallingBlock block = Bukkit.getWorld(world).spawnFallingBlock(new Location(Bukkit.getWorld(world), x1, y1, z1), Material.getMaterial(id), (byte) 0);
                		float x = (float) 0;
                        float y = (float) 1;
                        float z = (float) 0;
                        block.setVelocity(new Vector(x, y, z));
                        entities.add(block);
                        block.setDropItem(false);
            		} else {      		
            			FallingBlock block = Bukkit.getWorld(world).spawnFallingBlock(new Location(Bukkit.getWorld(world), x1, y1, z1), Material.getMaterial(material.toUpperCase()), (byte) 0);
	            		float x = (float) 0;
	                    float y = (float) 1;
	                    float z = (float) 0;
	                    block.setVelocity(new Vector(x, y, z));
	                    entities.add(block);
	                    block.setDropItem(false);
            		}
            	}
            }
        }, 0L, 40L);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void entityBlockForm(EntityChangeBlockEvent event){
		if (entities.contains(event.getEntity())){
			event.setCancelled(true);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0){
			sender.sendMessage(ChatColor.BLUE + "===== JumpingBlocks by BaranCODE =====");
			sender.sendMessage(ChatColor.AQUA + "/jumpingblock create <block type ID>" + ChatColor.BLUE + " - Create a JumpingBlock");
			sender.sendMessage(ChatColor.AQUA + "/jumpingblock delete <JumpingBlock ID>" + ChatColor.BLUE + " - Delete a JumpingBlock");
			sender.sendMessage(ChatColor.AQUA + "/jumpingblock deletelast" + ChatColor.BLUE + " - Delete the most recent JumpingBlock");
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("create")){
			if (!sender.hasPermission("jumpingblock.create")){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return true;
			}
			if (isInteger(args[1])){
				if (isValidMaterial(Integer.parseInt(args[1]))){
					getConfig().set("lastid", getConfig().getInt("lastid") + 1);
					Player player = (Player) sender;
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".world", player.getWorld().getName());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".x", player.getLocation().getBlockX());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".y", player.getLocation().getBlockY());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".z", player.getLocation().getBlockZ());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".type", Integer.parseInt(args[1]));
					saveConfig();
					player.sendMessage(ChatColor.GOLD + "You have created a JumpingBlock. The JumpingBlock ID is: " + getConfig().getInt("lastid"));
				} else {
					sender.sendMessage(ChatColor.RED + "That is not a valid material");
				}
			} else {
				if (isValidMaterial(args[1]).equals("good")){
					getConfig().set("lastid", getConfig().getInt("lastid") + 1);
					Player player = (Player) sender;
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".world", player.getWorld().getName());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".x", player.getLocation().getBlockX());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".y", player.getLocation().getBlockY());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".z", player.getLocation().getBlockZ());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".type", args[1]);
					saveConfig();
					player.sendMessage(ChatColor.GOLD + "You have created a JumpingBlock. The JumpingBlock ID is: " + getConfig().getInt("lastid"));
				} else if (isValidMaterial(args[1]).equals("bad")){
					sender.sendMessage(ChatColor.RED + "That is not a valid material");
				} else {
					getConfig().set("lastid", getConfig().getInt("lastid") + 1);
					Player player = (Player) sender;
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".world", player.getWorld().getName());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".x", player.getLocation().getBlockX());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".y", player.getLocation().getBlockY());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".z", player.getLocation().getBlockZ());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".type", isValidMaterial(args[1]));
					saveConfig();
					player.sendMessage(ChatColor.GOLD + "You have created a JumpingBlock. The JumpingBlock ID is: " + getConfig().getInt("lastid"));
				}
			}
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("delete")){
			if (!sender.hasPermission("jumpingblock.delete")){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return true;
			}
			getConfig().set("blocks." + Integer.parseInt(args[1]), "");
			if (Integer.parseInt(args[1]) == getConfig().getInt("lastid")){
				getConfig().set("lastid", getConfig().getInt("lastid") - 1);
			}
			saveConfig();
			sender.sendMessage(ChatColor.GOLD + "You have deleted a JumpingBlock");
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("deletelast")){
			if (!sender.hasPermission("jumpingblock.delete")){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return true;
			}
			int id = getConfig().getInt("lastid");
			if (id == 0){
				sender.sendMessage(ChatColor.RED + "There are no JumpingBlocks left");
				return true;
			}
			getConfig().set("blocks." + id, "");
			getConfig().set("lastid", id - 1);
			saveConfig();
			sender.sendMessage(ChatColor.GOLD + "You have deleted the most recently created JumpingBlock");
			return true;
		}
		return false;
	}
	
	boolean isInteger(String s){
		try{
			Integer.parseInt(s);
		} catch (NumberFormatException e){
			return false;
		}
		return true;
	}
	
	boolean isValidMaterial(int id){
	    try{
	        for(Material m : Material.values()){
	            if (id == m.getId()){
	                return true;
	            }
	        }
	        return false;
	    } catch (Exception e){
	    	return false;
	    }
	}
	
	String isValidMaterial(String name){
	    try{
	        for(Material m : Material.values()){
	            if (name.equalsIgnoreCase(m.name())){
	                return "good";
	            } else if ((m.name().replaceAll("_", "")).equalsIgnoreCase(name)){
	            	return m.name();
	            }
	        }
	        return "bad";
	    } catch (Exception e){
	    	return "bad";
	    }
	}
}
