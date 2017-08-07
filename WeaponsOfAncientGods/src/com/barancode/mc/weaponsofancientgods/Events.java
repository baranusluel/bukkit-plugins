package com.barancode.mc.weaponsofancientgods;



import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class Events implements Listener{
	
	  public Main plugin;
	  List<Arrow> arrows = new LinkedList<Arrow>();
		
	  public Events(Main plugin)
	  {
	    this.plugin = plugin;
	  }

	  @EventHandler
	  public void blockplace(BlockPlaceEvent event){
		  if (event.getPlayer().getItemInHand().equals(plugin.trident)){
			  Block block = event.getBlock().getLocation().getWorld().getBlockAt(event.getBlock().getLocation());
			  block.setType(Material.WATER);
			  event.setCancelled(true);
		  }
	  }
	  
	  @EventHandler
	  public void pvp(EntityDamageByEntityEvent event){
		  if (event.getDamager().getType().equals(EntityType.PLAYER)) {
			  Player player = (Player) event.getDamager();
			  if (player.getItemInHand().equals(plugin.hammer)){
				 plugin.knockback(player, event.getEntity());
			  } else if (player.getItemInHand().equals(plugin.lightning)){
				  player.getWorld().strikeLightning(event.getEntity().getLocation());
			  } else if (player.getItemInHand().equals(plugin.trident)){
				  Block block = event.getEntity().getWorld().getBlockAt(event.getEntity().getLocation());
				  block.setType(Material.WATER);
			  } else if (player.getItemInHand().equals(plugin.staff)){
				  event.setDamage(100);
			  } else if (player.getItemInHand().equals(plugin.sceptre)){
				  Location loc = player.getLocation();
				  loc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
				  FallingBlock block = player.getWorld().spawnFallingBlock(loc, Material.SNOW, (byte) 0);
			      Location l = event.getEntity().getLocation().subtract(loc);
			      double distance = event.getEntity().getLocation().distance(loc);
			      Vector v = l.toVector().multiply(3/distance);
			      block.setVelocity(v);
			      event.setDamage(10);
			  } else if (player.getItemInHand().equals(plugin.spear)){
				  Location loc = player.getLocation();
				  loc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
				  FallingBlock block = player.getWorld().spawnFallingBlock(loc, Material.ARROW, (byte) 0);
			      Location l = event.getEntity().getLocation().subtract(loc);
			      double distance = event.getEntity().getLocation().distance(loc);
			      Vector v = l.toVector().multiply(3/distance);
			      block.setVelocity(v);
			      event.setDamage(20);
			      player.setItemInHand(new ItemStack(Material.AIR));
			  }
		  }
	  }
	  
	  @EventHandler
	  public void interact(PlayerInteractEvent event){
		  final Player player = event.getPlayer();
		  if (player.getItemInHand().equals(plugin.hammer)){
			 if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK){
				 Location loc = event.getClickedBlock().getLocation();
				 Location loc2;
				 for (int x = -2; x < 3; x++){
					 for (int y = -2; y < 3; y++){
						 for (int z = -2; z < 3; z++){
							 if (y == 2 || y == -2){
								 if (x == 1 || x == 0 || x == -1 || z == 1 || z == 0 || z == -1){
									 loc2 = new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
									 plugin.bounceBlock(loc2.getWorld().getBlockAt(loc2));
								 }
							 } else {
								 loc2 = new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
								 plugin.bounceBlock(loc2.getWorld().getBlockAt(loc2));
							 }
						 }
					 }
				 }
			 }
		  } else if (player.getItemInHand().equals(plugin.lightning)){
			  player.getWorld().strikeLightning(player.getTargetBlock(null, 200).getLocation());
		  } else if (player.getItemInHand().equals(plugin.trident)){
			  Location loc = player.getLocation();
			  loc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
			  FallingBlock block = player.getWorld().spawnFallingBlock(loc, Material.WATER, (byte) 0);
		      Location l = player.getTargetBlock(null, 200).getLocation().subtract(loc);
		      double distance = player.getTargetBlock(null, 200).getLocation().distance(loc);
		      Vector v = l.toVector().multiply(5/distance);
		      block.setVelocity(v);
		  } else if (player.getItemInHand().equals(plugin.staff)){
			  Location loc = player.getTargetBlock(null, 200).getLocation();
			  loc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
			  player.getWorld().spawnCreature(loc, EntityType.SKELETON);
		  } else if (player.getItemInHand().equals(plugin.sceptre)){
			  Location loc = player.getLocation();
			  loc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
			  FallingBlock block = player.getWorld().spawnFallingBlock(loc, Material.SNOW, (byte) 0);
		      Location l = player.getTargetBlock(null, 200).getLocation().subtract(loc);
		      double distance = player.getTargetBlock(null, 200).getLocation().distance(loc);
		      Vector v = l.toVector().multiply(5/distance);
		      block.setVelocity(v);
		      
		      final Location locfinal = player.getTargetBlock(null, 200).getLocation();
		      		      
		      BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						 Location loc2;
						 for (int x = -1; x < 2; x++){
							 for (int y = -1; y < 2; y++){
								 for (int z = -1; z < 2; z++){
									 if (y == 1 || y == -1){
										 if (x == 0 || z == 0){
											 loc2 = new Location(locfinal.getWorld(), locfinal.getBlockX() + x, locfinal.getBlockY() + y, locfinal.getBlockZ() + z);
											 plugin.bounceBlock(loc2.getWorld().getBlockAt(loc2));
										 }
									 } else {
										 loc2 = new Location(locfinal.getWorld(), locfinal.getBlockX() + x, locfinal.getBlockY() + y, locfinal.getBlockZ() + z);
										 plugin.bounceBlock(loc2.getWorld().getBlockAt(loc2));
									 }
								 }
							 }
						 }
		            }
		        }, (long)(distance * 0.4));
	      } else if (player.getItemInHand().equals(plugin.spear)){
			  Location loc = player.getLocation();
			  loc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ());
		      Location l = player.getTargetBlock(null, 200).getLocation().subtract(loc);
		      double distance = player.getTargetBlock(null, 200).getLocation().distance(loc);
		      Vector v = l.toVector().multiply(5/distance);
			  Arrow block = player.getWorld().spawnArrow(loc, v);
		      player.setItemInHand(new ItemStack(Material.AIR));
		      
		      arrows.add(block);
		      
		      
		      
		      BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						 player.setItemInHand(plugin.spear);
		            }
		        }, (long)(distance));
	      }
		  
		  
		  
	  }
	  
	  
	  
	  @EventHandler
	  public void arrowEvent(EntityDamageEvent e){
		  if (e.getCause() == DamageCause.PROJECTILE){
			  
		  }
	  }
}
