package com.barancode.mc.commandercraft.swords;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener{
	
	ItemStack ddos = new ItemStack(Material.DIAMOND_SWORD, 1);
	ItemStack hangover = new ItemStack(Material.DIAMOND_SWORD, 1);
	ItemStack sundaymorning = new ItemStack(Material.GOLD_SWORD, 1);
	ItemStack xxx = new ItemStack(Material.DIAMOND_SWORD, 1);
	ItemStack noobbeater = new ItemStack(Material.DIAMOND_SWORD, 1);
	
	List<ItemMeta> items = new LinkedList<ItemMeta>();
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		ItemStack ddos = getConfig().getItemStack("ddos");
		ItemStack hangover = getConfig().getItemStack("hangover");
		ItemStack sundaymorning = getConfig().getItemStack("sundaymorning");
		ItemStack xxx = getConfig().getItemStack("xxx");
		ItemStack noobbeater = getConfig().getItemStack("noobbeater");
		if (ddos != null) items.add(ddos.getItemMeta());
		else items.add(this.ddos.getItemMeta());
		if (hangover != null) items.add(hangover.getItemMeta());
		else items.add(this.hangover.getItemMeta());
		if (sundaymorning != null) items.add(sundaymorning.getItemMeta());
		else items.add(this.sundaymorning.getItemMeta());
		if (xxx != null) items.add(xxx.getItemMeta());
		else items.add(this.xxx.getItemMeta());
		if (noobbeater != null) items.add(noobbeater.getItemMeta());
		else items.add(this.noobbeater.getItemMeta());
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player;
		if (sender instanceof Player) {
	        player = (Player) sender;
	    } else {
	        sender.sendMessage(ChatColor.RED + "You must be a player!");
	        return true;
	    }
		
		if (args.length != 1) return false;
		
		String name;
		if (args[0].equals("ddos")) name = "DDoS";
		else if (args[0].equals("hangover")) name = "HANG OVER";
		else if (args[0].equals("sundaymorning")) name = "SUNDAY MORNING";
		else if (args[0].equals("xxx")) name = "XXX";
		else if (args[0].equals("noobbeater")) name = "NOOB BEATER";
		else name = "";
		
		ItemStack item = player.getItemInHand();
		ItemMeta meta = item.getItemMeta(); meta.setDisplayName(name); item.setItemMeta(meta);
		items.add(item.getItemMeta());
		getConfig().set(args[0], item);
		saveConfig();
		return true;
	}
	
	
	@EventHandler
	public void damageEvent(EntityDamageByEntityEvent event){
		getLogger().info("test");
		if (!(event.getDamager() instanceof Player)) return;
		if (!(event.getEntity() instanceof LivingEntity)) return;
		Player player = (Player)event.getDamager();
		LivingEntity entity = (LivingEntity) event.getEntity();
		if (!items.contains(player.getItemInHand().getItemMeta())) return;
		
		String DN = player.getItemInHand().getItemMeta().getDisplayName();
		if (DN.equals("DDoS BLADE")){
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * getConfig().getInt("ddos-time"), 10));
			player.getWorld().playSound(entity.getLocation(), Sound.GLASS, 1f, 1f);
		}
		else if (DN.equals("HANG OVER")) {
			getLogger().info("test");
			event.getEntity().setVelocity(new Vector(0, 4, 0));
		}
		else if (DN.equals("SUNDAY MORNING")) entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * getConfig().getInt("sundaymorning-time"), 1));
		else if (DN.equals("XXX")) entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * getConfig().getInt("xxx-time"), 1));
		else if (DN.equals("NOOB BEATER")){
			entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * getConfig().getInt("noobbeater-time"), 1));
			player.getWorld().playSound(entity.getLocation(), Sound.SPLASH, 1f, 1f);
		}
	}
}
