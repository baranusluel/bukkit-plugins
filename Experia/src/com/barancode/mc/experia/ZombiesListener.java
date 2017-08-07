package com.barancode.mc.experia;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ZombiesListener implements Listener{
	
	Main plugin;
	
	public ZombiesListener(Main plugin){
		this.plugin = plugin;
	}
	
	/*@EventHandler(priority = EventPriority.HIGHEST)
	public void combust(EntityCombustEvent event){
		if ((event.getEntityType().equals(EntityType.ZOMBIE) || event.getEntityType().equals(EntityType.GIANT))){
            event.getEntity().setFireTicks(0);
            event.setCancelled(true);
        }
	}*/
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void spawn(CreatureSpawnEvent event){
		if (event.getLocation().getWorld() != Bukkit.getWorld("Experia-game")) {
			event.setCancelled(true);
			return;
		}
		if (event.getEntityType() == EntityType.ZOMBIE){
			if (event.getSpawnReason() == SpawnReason.NATURAL){
				event.setCancelled(true);
				return;
			}
			Zombie zombie = (Zombie) event.getEntity();
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6100, 2));
		} else if (event.getEntityType() == EntityType.SKELETON){
			if (event.getSpawnReason() == SpawnReason.NATURAL){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void death(EntityDeathEvent event){
		Entity entity = event.getEntity();
		if (entity.getType() == EntityType.GIANT){
			//Bukkit.broadcastMessage(plugin.utils.replace(plugin.getConfig().getString("giantdeathmessage")));
			event.setDroppedExp(370);
			plugin.itemmanager.dropGiantLoot(entity);
		}
	}
}
