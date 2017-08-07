package com.barancode.mc.experia;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class MyItemsCommands implements CommandExecutor {
	
	Main plugin;
	
	public MyItemsCommands(Main plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0){
			sender.sendMessage(ChatColor.BLUE + "-------------MyItems By BaranCODE-------------");
			sender.sendMessage(ChatColor.AQUA + "/mi get <item> [player]");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Gives you an item");
			sender.sendMessage(ChatColor.AQUA + "/mi list");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Lists the items");
			sender.sendMessage(ChatColor.AQUA + "/mi create <item name> <item's display name> <item's ID> [-g] [-s]");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Create a new custom item");
			sender.sendMessage(ChatColor.AQUA + "/mi setcommand <item name> <event> <command>");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Set the command of an item for a certain event");
			sender.sendMessage(ChatColor.AQUA + "/mi setlore <item name> <lore>");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Set the lore of an item");
			sender.sendMessage(ChatColor.AQUA + "/mi delete <item name>");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Delete an item. NOT REVERSABLE");
			return true;
		} else if (args.length == 1){
			if (args[0].equalsIgnoreCase("list")){
				if (!sender.hasPermission("myitems.list")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				sender.sendMessage(ChatColor.BLUE + "The items are:");
				for (File file : plugin.getDataFolder().listFiles()){
					if (!file.getName().equals("config.yml") && !file.getName().equals("data.yml")){
						String name = file.getName();
						name = name.replace(".txt", "");
						sender.sendMessage(ChatColor.AQUA + name);
					}
				}
				return true;
			}
		} else if (args.length == 2){
			if (args[0].equalsIgnoreCase("get")){
				Player player;
				if (sender instanceof Player) {
			        player = (Player) sender;
			    } else {
			        sender.sendMessage(ChatColor.RED + "You must be a player!");
			        return true;
			    }
				
				if (!sender.hasPermission("myitems.get")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				
				if (!plugin.MyItemsItems.containsValue(args[1])) {
					sender.sendMessage(ChatColor.RED + "That item does not exist!");
					return true;
				}
				
				String displayname = plugin.ItemFile.read(args[1], "displayname");
				displayname = plugin.utils.replace(displayname);
				
				String lore = plugin.ItemFile.read(args[1], "lore");
				String[] loreparts = lore.split("&&");
				List<String> list = new LinkedList<String>();
				for (int i = 0; i < loreparts.length; i++){
					list.add(plugin.utils.replace(loreparts[i]));
				}
				
				String id = plugin.ItemFile.read(args[1], "id");
				String[] idparts = id.split(":");
				
				ItemStack itemstack = new ItemStack(Material.AIR, 1);
				
				if (idparts.length == 2){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1]));
				} else if (idparts.length == 1){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1);
				}
				
				PlayerInventory inventory = player.getInventory();
				
				ItemMeta meta = itemstack.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(list);
				itemstack.setItemMeta(meta);
				inventory.addItem(itemstack);
				sender.sendMessage(ChatColor.GREEN + "You have been given the " + args[1] + " item");
				return true;
			} else if (args[0].equalsIgnoreCase("delete")){
				if (!sender.hasPermission("myitems.delete")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				File saveTo = new File(plugin.getDataFolder(), args[1] + ".txt");
				saveTo.delete();
				sender.sendMessage(ChatColor.GREEN + "That item has been deleted!");
				return true;
			}
		} else {
			if (args[0].equalsIgnoreCase("get") && args.length == 3){
				Player player = Bukkit.getPlayer(args[2]);
				if (!player.isOnline()){
					sender.sendMessage(ChatColor.RED + "That player isn't online");
					return true;
				}
				
				if (!sender.hasPermission("myitems.get")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				
				if (!plugin.MyItemsItems.containsValue(args[1])) {
					sender.sendMessage(ChatColor.RED + "That item does not exist!");
					return true;
				}
				
				String displayname = plugin.ItemFile.read(args[1], "displayname");
				displayname = plugin.utils.replace(displayname);
				
				String lore = plugin.ItemFile.read(args[1], "lore");
				String[] loreparts = lore.split("&&");
				List<String> list = new LinkedList<String>();
				for (int i = 0; i < loreparts.length; i++){
					list.add(plugin.utils.replace(loreparts[i]));
				}
				
				String id = plugin.ItemFile.read(args[1], "id");
				String[] idparts = id.split(":");
				
				ItemStack itemstack = new ItemStack(Material.AIR, 1);
				
				if (idparts.length == 2){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1]));
				} else if (idparts.length == 1){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1);
				}
				
				PlayerInventory inventory = player.getInventory();
				
				ItemMeta meta = itemstack.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(list);
				itemstack.setItemMeta(meta);
				int slot = Integer.parseInt(plugin.ItemFile.read(args[1], "slot"));
				inventory.setItem(slot, itemstack);
				//sender.sendMessage(ChatColor.GREEN + "You have given the " + args[1] + " item");
				return true;
			}
			
			
			if (args[0].equalsIgnoreCase("create")){				
				if (!sender.hasPermission("myitems.create")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				plugin.ItemFile.write(args[1], "displayname", args[2]);
				plugin.ItemFile.write(args[1], "id", args[3]);
				plugin.ItemFile.write(args[1], "lore", "Default item lore");
				
				String id = args[3];
				String[] idparts = id.split(":");
				
				ItemStack itemstack = new ItemStack(Material.AIR, 1);
				
				if (idparts.length == 2){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1]));
				} else if (idparts.length == 1){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1);
				}
				
				ItemMeta meta = itemstack.getItemMeta();
				meta.setDisplayName(args[2]);
				List<String> list = new LinkedList<String>();
				list.add("Default item lore");
				meta.setLore(list);
				itemstack.setItemMeta(meta);
				
				plugin.MyItemsItems.put(meta, args[1]);
				sender.sendMessage(ChatColor.GREEN + "You have created an item");
				return true;
			}
			else if (args[0].equalsIgnoreCase("setcommand")){
				if (!sender.hasPermission("myitems.setcommand")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				String finalString = "";
    			for (int i = 3; i < args.length; i++) {
    			    finalString += args[i] + ' ';
    			}
    			finalString = finalString.trim();
    			String event = args[2];
    			if (plugin.events.contains(event.toLowerCase())){
    				plugin.ItemFile.write(args[1], args[2], finalString);
    				sender.sendMessage(ChatColor.GREEN + "You have set a command for the event " + event + " for the item " + args[1]);
    				return true;
    			} else {
    				if (event.equalsIgnoreCase("rightclickevent")){
    					plugin.ItemFile.write(args[1], "rightclickentityevent", finalString);
    					plugin.ItemFile.write(args[1], "rightclickblockevent", finalString);
    					plugin.ItemFile.write(args[1], "rightclickairevent", finalString);
	    				sender.sendMessage(ChatColor.GREEN + "You have set a command for the event group rightclickevent for the item " + args[1]);
	    				return true;
    				} else if (event.equalsIgnoreCase("leftclickevent")){
    					plugin.ItemFile.write(args[1], "damageentityevent", finalString);
    					plugin.ItemFile.write(args[1], "leftclickblockevent", finalString);
    					plugin.ItemFile.write(args[1], "leftclickairevent", finalString);
	    				sender.sendMessage(ChatColor.GREEN + "You have set a command for the event group leftclickevent for the item " + args[1]);
	    				return true;
    				}
    			}
    			sender.sendMessage(ChatColor.RED + "That is not a valid event");
				return true;
			} else if (args[0].equalsIgnoreCase("setlore")){
				if (!sender.hasPermission("myitems.setlore")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				String finalString = "";
    			for (int i = 2; i < args.length; i++) {
    			    finalString += args[i] + ' ';
    			}
    			finalString = finalString.trim();
    			plugin.ItemFile.write(args[1], "lore", finalString);
				
				
				String displayname = plugin.ItemFile.read(args[1], "displayname");
				displayname = plugin.utils.replace(displayname);
				
				String[] loreparts = finalString.split("&&");
				List<String> list = new LinkedList<String>();
				for (int i = 0; i < loreparts.length; i++){
					list.add(plugin.utils.replace(loreparts[i]));
				}
				
				String id = plugin.ItemFile.read(args[1], "id");
				String[] idparts = id.split(":");
				
				ItemStack itemstack = new ItemStack(Material.AIR, 1);
				
				if (idparts.length == 2){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1]));
				} else if (idparts.length == 1){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1);
				}
				
				ItemMeta meta = itemstack.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(list);
				
				
				plugin.MyItemsItems.put(meta, args[1]);
				
				sender.sendMessage(ChatColor.GREEN + "You have set the lore of the item " + args[1]);
				return true;
			}
		}
		return false;
	}
}
