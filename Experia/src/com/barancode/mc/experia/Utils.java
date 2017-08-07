package com.barancode.mc.experia;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;

public class Utils {
	  Main plugin;
	  List<String> enchantments = new LinkedList<String>();
	  Random random = new Random();

	  public Utils(Main plugin)
	  {
	    this.plugin = plugin;
	    enchantments.add("ARROW_DAMAGE");
	    enchantments.add("ARROW_FIRE");
	    enchantments.add("ARROW_INFINITE");
	    enchantments.add("ARROW_KNOCKBACK");
	    enchantments.add("DAMAGE_ALL");
	    enchantments.add("DAMAGE_ARTHROPODS");
	    enchantments.add("DAMAGE_UNDEAD");
	    enchantments.add("DIG_SPEED");
	    enchantments.add("DURABILITY");
	    enchantments.add("FIRE_ASPECT");
	    enchantments.add("KNOCKBACK");
	    enchantments.add("LUCK");
	    enchantments.add("PROTECTION_ENVIRONMENTAL");
	    enchantments.add("PROTECTION_EXPLOSIONS");
	    enchantments.add("PROTECTION_FALL");
	    enchantments.add("PROTECTION_FIRE");
	    enchantments.add("PROTECTION_PROJECTILE ");
	    enchantments.add("SILK_TOUCH");
	    enchantments.add("THORNS");
	    enchantments.add("WATER_WORKER");
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
    
    public UUID getUUID(String player) {
    	return Main.uuidDatabase.getUUID(player);
    }
    
    public String getRandomEnchantment(){
    	int i = random.nextInt(enchantments.size());
    	return enchantments.get(i);
    }
}
