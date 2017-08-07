package com.barancode.mc.ddg.survival;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {
	  Main plugin;

	  public Utils(Main plugin)
	  {
	    this.plugin = plugin;
	  }
	  
    public String replace(String message){
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
    
    public boolean isEmerald(ItemStack item){
    	if (item == null) return false;
    	ItemMeta im = item.getItemMeta();
    	if (im == null) return false;
    	List<String> lore = im.getLore();
    	if (lore == null) return false;
    	
    	ItemStack emerald = plugin.emerald;
    	ItemMeta imsecond = emerald.getItemMeta();
    	if (lore.equals(imsecond.getLore()) && item.getType().equals(emerald.getType())) return true;
    	else return false;
    }
}
