package com.barancode.myitems;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Commands implements CommandExecutor {
	Main plugin;
	
	public Commands(Main plugin){
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0){
			sender.sendMessage(ChatColor.BLUE + "-------------MyItems By BaranCODE-------------");
			sender.sendMessage(ChatColor.AQUA + "/mi get <item> [player]");
			sender.sendMessage(ChatColor.DARK_GREEN + "^ Gives the player a defineditem");
			sender.sendMessage(ChatColor.AQUA + "/mi list");
			sender.sendMessage(ChatColor.DARK_GREEN + "^ Lists the defined items");
			sender.sendMessage(ChatColor.AQUA + "/mi create <item name> <item's material name> [-g] [-s]");
			sender.sendMessage(ChatColor.RED + " OR " + ChatColor.AQUA + "/mi create <item name> current [-g] [-s]");
			sender.sendMessage(ChatColor.DARK_GREEN + "^ Create a new custom item");
			sender.sendMessage(ChatColor.AQUA + "/mi setcommand <item name> <event> <command>");
			sender.sendMessage(ChatColor.DARK_GREEN + "^ Set a command for an item that will execute on an event");
			sender.sendMessage(ChatColor.AQUA + "/mi setlore <item name> <display name>");
			sender.sendMessage(ChatColor.DARK_GREEN + "^ Set the display name of an item");
			sender.sendMessage(ChatColor.AQUA + "/mi setlore <item name> <lore>");
			sender.sendMessage(ChatColor.DARK_GREEN + "^ Set the lore of an item");
			sender.sendMessage(ChatColor.AQUA + "/mi delete <item name>");
			sender.sendMessage(ChatColor.DARK_GREEN + "^ Delete an item. NOT REVERSIBLE");
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("list")){
			if (!sender.hasPermission("myitems.list")){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return true;
			}
			sender.sendMessage(ChatColor.BLUE + "The items defined in MyItems are:");
			for (String s : plugin.items.keySet()){
				sender.sendMessage(ChatColor.AQUA + "- " + s);
			}
			return true;
		} else if ((args.length == 2 || args.length == 3) && args[0].equalsIgnoreCase("get")){
			if (!sender.hasPermission("myitems.get")){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return true;
			}
			if (!plugin.items.containsKey(args[1])){
				sender.sendMessage(ChatColor.RED + "That item does not exist!");
				return true;
			}
			MyItem item = plugin.items.get(args[1]);
			
			Player player;
			if (args.length == 2){
				if (!(sender instanceof Player)){
					sender.sendMessage(ChatColor.RED + "You must be a player!");
			        return true;
				}
				player = (Player) sender;
			} else {
				if (!Bukkit.getOfflinePlayer(args[2]).isOnline()){
					sender.sendMessage(ChatColor.RED + "That player isn't online");
					return true;
				}
				player = Bukkit.getPlayer(args[2]);
			}
			
			ItemStack stack = new ItemStack(item.material, 1, (short)item.damage, (byte)item.data);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(item.displayname);
			meta.setLore(Arrays.asList(item.lore));
			stack.setItemMeta(meta);
			for (Enchantment e : item.enchantments.keySet())
				stack.addEnchantment(e, item.enchantments.get(e));
			player.getInventory().addItem(stack);
			sender.sendMessage(ChatColor.AQUA + "The item has been given");
			if (args.length == 3 && sender instanceof Player && !player.equals((Player)sender))
				player.sendMessage(ChatColor.AQUA + "You have been given an item");
			return true;
		}
		return false;
	}
}
