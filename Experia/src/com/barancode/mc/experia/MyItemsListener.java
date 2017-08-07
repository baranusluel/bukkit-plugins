package com.barancode.mc.experia;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class MyItemsListener implements Listener {
	
	Main plugin;
	Random random = new Random();
	
	public MyItemsListener(Main plugin){
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void interactEvent(PlayerInteractEvent event){
		if (event.getAction() == Action.PHYSICAL){
			event.setCancelled(true);
			return;
		}
		ItemStack itemstack = event.getPlayer().getItemInHand();
		if (plugin.MyItemsItems.containsKey(itemstack.getItemMeta())){
			CommandSender sender = event.getPlayer();
			
			if (!sender.hasPermission("myitems.use." + plugin.MyItemsItems.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			
			if (event.getAction() == Action.LEFT_CLICK_AIR){
				String command = plugin.ItemFile.read(plugin.MyItemsItems.get(itemstack.getItemMeta()), "leftclickairevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
				}
			} else if (event.getAction() == Action.RIGHT_CLICK_AIR){
				String command = plugin.ItemFile.read(plugin.MyItemsItems.get(itemstack.getItemMeta()), "rightclickairevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
				}
				event.setCancelled(true);
			} else if (event.getAction() == Action.LEFT_CLICK_BLOCK){
				String command = plugin.ItemFile.read(plugin.MyItemsItems.get(itemstack.getItemMeta()), "leftclickblockevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
				}
			} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
				String command = plugin.ItemFile.read(plugin.MyItemsItems.get(itemstack.getItemMeta()), "rightclickblockevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
				}
				event.setCancelled(true);
			}
		} else if (itemstack.getType() == Material.FLINT_AND_STEEL){
			Location loc = event.getPlayer().getLocation(); loc.setY(loc.getY() + 2);
			Entity e = event.getPlayer().getWorld().spawnEntity(loc, EntityType.FIREBALL);
			e.setVelocity(event.getPlayer().getLocation().getDirection());
			((Fireball)e).setShooter((ProjectileSource)event.getPlayer());
			((Fireball)e).setIsIncendiary(false);
			((Fireball)e).setYield(0);
			event.setCancelled(true);
			itemstack.setDurability((short)(itemstack.getDurability() + 1));
			if (itemstack.getDurability() >= itemstack.getType().getMaxDurability()) event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
			event.getPlayer().updateInventory();
		} else if (event.getClickedBlock() != null && event.getClickedBlock().getTypeId() == 33 && event.getClickedBlock().getData() == (byte)6){
        	event.setCancelled(true);
        	plugin.itemmanager.dropChestLoot(event.getClickedBlock());
		}
	}
	
	
	@EventHandler
	public void entityinteractEvent(PlayerInteractEntityEvent event){
		ItemStack itemstack = event.getPlayer().getItemInHand();
		if (plugin.MyItemsItems.containsKey(itemstack.getItemMeta())){
			CommandSender sender = event.getPlayer();
			
			if (!sender.hasPermission("myitems.use." + plugin.MyItemsItems.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = plugin.ItemFile.read(plugin.MyItemsItems.get(itemstack.getItemMeta()), "rightclickentityevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					if (event.getRightClicked().getType() == EntityType.PLAYER)
						s = s.replaceAll("<clicked>", ((Player) event.getRightClicked()).getName());
					else {
						String name = event.getRightClicked().getType().getName();
						s = s.replaceAll("<clicked>", name);
					}
					Bukkit.getServer().dispatchCommand(sender, s);
				}
			}
		}
	}
	
	
	@EventHandler
	public void damageEvent(EntityDamageByEntityEvent event){
		if (event.getEntity().getWorld().getName().equalsIgnoreCase("Experia")){
			event.setCancelled(true);
			return;
		}
		
		if (event.getEntity() instanceof Player && plugin.invincible.contains(((Player)event.getEntity()).getName())){
			if (event.getDamager() instanceof Player){
				((Player)event.getDamager()).sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.cannot-hit")).replaceAll("<player>", ((Player)event.getEntity()).getName()));
			}
			event.setCancelled(true);
			return;
		}		
		Player tempP;
		if (event.getDamager() instanceof Player){
			tempP = (Player)event.getDamager();
		} else if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player){
			tempP = (Player)((Projectile)event.getDamager()).getShooter();
			if (event.getDamager() instanceof Fireball){
				event.getEntity().setFireTicks(100);
			}
		} else {
			return;
		}
		final Player player = tempP;
		
		if (event.getEntity() instanceof Giant){
			boolean contains = false;
		    for (Iterator<GiantHit> it = plugin.giantHits.iterator(); it.hasNext(); ){
		    	GiantHit next = it.next();
				if (!next.player.equals(player.getName())) continue;
				if (next.giant != event.getEntity()) continue;
				contains = true;
				next.hits = next.hits + 1;
				if (next.hits > 7){
					it.remove();
					Location loc = event.getEntity().getLocation(); loc.setY(loc.getY() + 6);
					ParticleEffect.EXPLODE.display(loc, 3F, 3, 3F, 0.0000000001F, 10000);
					
					for (int i = 0; i < 2; i++){
						Location temploc = new Location(loc.getWorld(), loc.getX() + random.nextInt(6) - 3, loc.getY() - 4, loc.getZ() + random.nextInt(6) - 3);
						loc.getWorld().spawnEntity(temploc, EntityType.ZOMBIE);
					}
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("giantminionmessage")));
				}
		    }
		    if (!contains){
		    	plugin.giantHits.add(new GiantHit(player.getName(), 1, event.getEntity()));
		    }
		}
		
		if (event.getDamager() instanceof Player){
			ItemStack itemstack = player.getItemInHand();
			if (plugin.MyItemsItems.containsKey(itemstack.getItemMeta())){
				CommandSender sender = player;
				
				if (!sender.hasPermission("myitems.use." + plugin.MyItemsItems.get(itemstack.getItemMeta()))){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return;
				}
				String command = plugin.ItemFile.read(plugin.MyItemsItems.get(itemstack.getItemMeta()), "damageentityevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", player.getName());
						if (event.getEntity().getType() == EntityType.PLAYER)
							s = s.replaceAll("<clicked>", ((Player) event.getEntity()).getName());
						else {
							String name = event.getEntity().getType().getName();
							s = s.replaceAll("<clicked>", name);
						}
						Bukkit.getServer().dispatchCommand(sender, s);
					}
					return;
				}
			}
		}
		
		if (!(event.getEntity() instanceof Player)){
			return;
		}
		final Player damaged = (Player)event.getEntity();
		
		String damagerTeam = plugin.teamMembers.get(player.getName());
		String damagedTeam = plugin.teamMembers.get(damaged.getName());
		System.out.println(damagerTeam + "  " + damagedTeam);
		if (damagerTeam != null && !damagerTeam.equals("") && damagedTeam != null && !damagedTeam.equals("")){
			System.out.println("test");
			if (damagerTeam.equals(damagedTeam)){
				event.setCancelled(true);
				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-hit")));
				return;
			}
		}
		
		if (!plugin.PVPPlayers.contains(player.getName())){
			player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("combat-messages.enter-combat")).replaceAll("<player>", damaged.getName()));
			plugin.PVPPlayers.add(player.getName());
		}
		if (!plugin.PVPPlayers.contains(damaged.getName())){
			damaged.sendMessage(plugin.utils.replace(plugin.getConfig().getString("combat-messages.enter-combat")).replaceAll("<player>", player.getName()));
			plugin.PVPPlayers.add(damaged.getName());
		}
		
		if (plugin.PVPSchedulers.containsKey(player.getName())){
			plugin.scheduler.cancelTask(plugin.PVPSchedulers.get(player.getName()));
		}
		if (plugin.PVPSchedulers.containsKey(damaged.getName())){
			plugin.scheduler.cancelTask(plugin.PVPSchedulers.get(damaged.getName()));
		}
		
        int id1 = plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run(){
            	plugin.PVPPlayers.remove(player.getName());
            	plugin.PVPSchedulers.remove(player.getName());
            	player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("combat-messages.stop-combat")));
            }
        }, 60 * 20L);
        plugin.PVPSchedulers.put(player.getName(), id1);
        
        int id2 = plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run(){
            	plugin.PVPPlayers.remove(damaged.getName());
            	plugin.PVPSchedulers.remove(damaged.getName());
            	damaged.sendMessage(plugin.utils.replace(plugin.getConfig().getString("combat-messages.stop-combat")));
            }
        }, 60 * 20L);
        plugin.PVPSchedulers.put(damaged.getName(), id2);
	}
}
