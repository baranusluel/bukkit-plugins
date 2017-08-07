package com.barancode.mc.lobby;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import com.barancode.mc.ddg.survival.Menu;

public class Main extends JavaPlugin implements Listener{
	
	List<String> playerswithspeed = new LinkedList<String>();
	List<String> viewingmessages = new LinkedList<String>();
	ItemStack item = new ItemStack(Material.COMPASS);
	ItemStack menuitem = new ItemStack(Material.INK_SACK, 1, (byte)4);
	boolean red = true;
	BukkitScheduler scheduler;
	Menu menu;
	
	public void onEnable(){
		scheduler = Bukkit.getServer().getScheduler();
		
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new LinkedList<String>();
		String s = replaceColors(getConfig().getString("enderpearllore"));
		for (String spart : s.split("&&")){
			lore.add(spart);
		}
		meta.setLore(lore);
		meta.setDisplayName(replaceColors(getConfig().getString("enderpearlname")));
		item.setItemMeta(meta);
		
		ItemMeta menumeta = menuitem.getItemMeta();
		List<String> menulore = new LinkedList<String>();
		String lorecombined = replaceColors(getConfig().getString("menulore"));
		for (String lorepart : lorecombined.split("&&")){
			menulore.add(lorepart);
		}
		menumeta.setLore(menulore);
		menumeta.setDisplayName(replaceColors(getConfig().getString("menuname")));
		menuitem.setItemMeta(menumeta);
		
		menu = new Menu(replaceColors(getConfig().getString("menuname")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(final Menu.OptionClickEvent event) {
            	
                event.setWillClose(true);
            }
        }, this);
		
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run(){
        		if (red){
                	for (Player p : Bukkit.getOnlinePlayers()){
                		if (!p.getWorld().getName().equals("NewHub")) {
                			if (viewingmessages.contains(p.getName())) viewingmessages.remove(p.getName());
                			continue;
                		}
        				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, getConfig().getInt("speedamplifier")), true);
                		if (viewingmessages.contains(p.getName()) && p.getItemInHand().getType() == Material.COMPASS){
	            			ItemStack localitem = p.getItemInHand();
	            			ItemMeta meta = localitem.getItemMeta();
	            			if (meta.getDisplayName().startsWith(ChatColor.RED + "\u258b ")){
	            				meta.setDisplayName(meta.getDisplayName().replaceAll(ChatColor.RED + "\u258b ", "  "));
	            			}
	            			localitem.setItemMeta(meta);
	            			p.setItemInHand(localitem);
                		} else {
                			if (p.getItemInHand().getType() == Material.AIR || p.getItemInHand() == null) p.setItemInHand(item);
                		}
                	}
        			red = false;
        		} else {
                	for (Player p : Bukkit.getOnlinePlayers()){
                		if (!p.getWorld().getName().equals("NewHub")) {
                			if (viewingmessages.contains(p.getName())) viewingmessages.remove(p.getName());
                			continue;
                		}
        				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, getConfig().getInt("speedamplifier")), true);
                		if (viewingmessages.contains(p.getName()) && p.getItemInHand().getType() == Material.COMPASS){
	            			ItemStack localitem = p.getItemInHand();
	            			ItemMeta meta = localitem.getItemMeta();
	            			int i = 0;
	            			String newstring = "";
	            			if (meta.getDisplayName().startsWith("  ")){
		            			for (char c : meta.getDisplayName().toCharArray()){
		            				if (i > 1) newstring = newstring + c;
		            				i++;
		            			}
		            			meta.setDisplayName(ChatColor.RED + "\u258b " + ChatColor.RESET + newstring);
	            			} else {
	            				meta.setDisplayName(ChatColor.RED + "\u258b " + ChatColor.RESET + meta.getDisplayName());
	            			}
	            			localitem.setItemMeta(meta);
	            			p.setItemInHand(localitem);
                		} else {
                			if (p.getItemInHand().getType() == Material.AIR || p.getItemInHand() == null) p.setItemInHand(item);
                		}
                	}
        			red = true;
        		}
        	}
        }, 0L, 20L);
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent event){
		if (event.getPlayer().getWorld().getName().equals("NewHub")){
			if (event.getPlayer().getItemInHand().getType() == item.getType()){
				Player p = event.getPlayer();
	            if(!p.hasMetadata("hiding")) {
	                for(Player ps : Bukkit.getOnlinePlayers()) {
	                    p.hidePlayer(ps);
	                }
	                p.setMetadata("hiding", new FixedMetadataValue(this, true));
	                p.sendMessage(replaceColors(getConfig().getString("hide")));
	            } else {
	                for(Player ps : Bukkit.getOnlinePlayers()) {
	                    p.showPlayer(ps);
	                }
	                p.removeMetadata("hiding", this);
	                p.sendMessage(replaceColors(getConfig().getString("show")));
	            }
	            event.setCancelled(true);
			} else if (event.getPlayer().getItemInHand().getType() == menuitem.getType()){
				
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void playerDamaged(EntityDamageEvent event){
		if (event.getEntity().getType() == EntityType.PLAYER && event.getEntity().getWorld().getName().equals("NewHub") && event.getCause() != DamageCause.VOID) event.setCancelled(true);
	}
	
	@EventHandler
	public void playerChangeHand(PlayerItemHeldEvent event){
		if (event.getPlayer().getWorld().getName().equals("NewHub") && event.getPreviousSlot() != event.getNewSlot()){
			if (!viewingmessages.contains(event.getPlayer().getName())){
				ItemStack localitem = item.clone();
				ItemMeta localmeta = localitem.getItemMeta();
				localmeta.setDisplayName(replaceColors(getConfig().getString("enderpearlname")));
				localitem.setItemMeta(localmeta);
				event.getPlayer().getInventory().setItem(event.getNewSlot(), localitem);
			} else {
				ItemStack localitem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
				event.getPlayer().getInventory().setItem(event.getNewSlot(), localitem);
			}
			event.getPlayer().getInventory().setItem(event.getPreviousSlot(), new ItemStack(Material.AIR));
		}
	}
    
	@EventHandler
	public void playerJoin(PlayerJoinEvent event){
		final Player player = event.getPlayer();
		int totaltime = 0;
		
		if (player.getWorld().getName().equals("NewHub")){
			
			player.getInventory().clear();
			
			viewingmessages.add(player.getName());
			
			
			for (int i = 1; i <= getConfig().getInt("messagecount"); i++){
				
				final int finali = i;
				
		        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
		            @Override
		            public void run(){
		            	if (finali != getConfig().getInt("messagecount")){
		            		displayMessage(player, replaceColors(getConfig().getString("messages." + finali + ".message")), false, getConfig().getInt("messages." + finali + ".time"));
		            	} else {
		            		displayMessage(player, replaceColors(getConfig().getString("messages." + finali + ".message")), true, getConfig().getInt("messages." + finali + ".time"));
		            	}
	            	}
		        }, totaltime * 20L);
		        
		        totaltime = totaltime + getConfig().getInt("messages." + i + ".time");
			}
		}
		
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p.hasMetadata("hiding")){
				p.hidePlayer(event.getPlayer());
			} else {
				p.showPlayer(event.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e){
		if (!e.getTo().getWorld().getName().equals("NewHub")){
			for (Player p : Bukkit.getOnlinePlayers()){
				e.getPlayer().showPlayer(p);
			}
		} else {
			e.getPlayer().getInventory().clear();
			e.getPlayer().setItemInHand(item);
			if (e.getPlayer().hasMetadata("hiding")){
				for (Player p : Bukkit.getOnlinePlayers()){
					e.getPlayer().hidePlayer(p);
				}
			} else {
				for (Player p : Bukkit.getOnlinePlayers()){
					e.getPlayer().showPlayer(p);
				}
			}
		}
	}
	
	@EventHandler
	public void projectileLaunch(ProjectileLaunchEvent event){
		if (event.getEntity().getWorld().getName().equals("NewHub") && event.getEntityType() == EntityType.ENDER_PEARL){
			event.setCancelled(true);
			if (!viewingmessages.contains(((Player)((Projectile)event.getEntity()).getShooter()).getName())){
				ItemStack localitem = item.clone();
				ItemMeta localmeta = localitem.getItemMeta();
				localmeta.setDisplayName(replaceColors(getConfig().getString("enderpearlname")));
				localitem.setItemMeta(localmeta);
				((Player)((Projectile)event.getEntity()).getShooter()).getInventory().setItemInHand(localitem);
			} else {
				ItemStack localitem = ;
				((Player)((Projectile)event.getEntity()).getShooter()).getInventory().setItem(event.getNewSlot(), localitem);
			}
		}
	}
	
	public void displayMessage(Player player, String message, boolean last, int time){
		ItemStack messageitem = item.clone();
		ItemMeta messagemeta = messageitem.getItemMeta();
		messagemeta.setDisplayName("  " + replaceColors(message));
		messageitem.setItemMeta(messagemeta);
		player.setItemInHand(messageitem);
		
		final Player finalplayer = player;
		
		if (last) {
	        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
	            @Override
	            public void run(){
	            	viewingmessages.remove(finalplayer.getName());
            	}
	        }, time * 20L);
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
