package me.empirewand;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Dagger
  implements Listener
{
  private Start plugin;
  Set<String> Sneaking = new HashSet();
  Set<String> Sprinting = new HashSet();

  public Dagger(Start instance) {
    this.plugin = instance;
  }

  @EventHandler
  public void onChange(PlayerItemHeldEvent event) {
    Player player = event.getPlayer();
    int slot = event.getNewSlot();
    ItemStack stack = player.getInventory().getItem(slot);
    if ((stack != null) && (stack.getType() == Material.RECORD_6) && (stack.getItemMeta().hasDisplayName()) && (stack.getItemMeta().getDisplayName().contains(ChatColor.GRAY + "Theros Dagger")))
      player.setFallDistance(-10000000.0F);
    else {
      player.setFallDistance(0.0F);
    }
    if (this.Sneaking.contains(player.getName())) {
      this.Sneaking.remove(player.getName());
      player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
    if (this.Sprinting.contains(player.getName())) {
      this.Sprinting.remove(player.getName());
      player.removePotionEffect(PotionEffectType.SPEED);
      player.removePotionEffect(PotionEffectType.JUMP);
    }
  }

  @EventHandler
  public void onSneakToggle(PlayerToggleSneakEvent event) {
    Player player = event.getPlayer();
    if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.RECORD_6) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.GRAY + "Theros Dagger")))
      if (event.isSneaking()) {
        this.Sneaking.add(player.getName());
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5000, 4));
      } else {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        if (this.Sneaking.contains(player.getName()))
          this.Sneaking.remove(player.getName());
      }
  }

  @EventHandler
  public void onSprintToggle(PlayerToggleSprintEvent event)
  {
    Player player = event.getPlayer();
    if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.RECORD_6) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.GRAY + "Theros Dagger")))
      if (event.isSprinting()) {
        this.Sprinting.add(player.getName());
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5000, 10));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 5000, 4));
      } else {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.JUMP);
        if (this.Sprinting.contains(player.getName()))
          this.Sprinting.remove(player.getName());
      }
  }

  @EventHandler
  public void onHit(EntityDamageByEntityEvent event)
  {
    Entity en = event.getDamager();
    if ((en instanceof Player)) {
      Player player = (Player)en;
      if ((player.getItemInHand() != null) && (player.getItemInHand().getType() == Material.RECORD_6) && (player.getItemInHand().getItemMeta().hasDisplayName()) && (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.GRAY + "Theros Dagger")) && 
        ((event.getEntity() instanceof LivingEntity))) {
        LivingEntity damaged = (LivingEntity)event.getEntity();
        damaged.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
        damaged.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 10));
        damaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 5));
      }
    }
  }
}