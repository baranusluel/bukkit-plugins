package me.empirewand;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class BloodWave
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public BloodWave(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, Player player) {
    final BlockIterator blocks = new BlockIterator(player);
    blocks.next();
    blocks.next();
    blocks.next();
    blocks.next();
    blocks.next();
    blocks.next();
    blocks.next();
    new BukkitRunnable() {
      public int timer = 0;

      public void run() {
        if (this.timer++ > 30) {
          cancel();
        }
        if (!blocks.hasNext()) {
          cancel();
        }
        Block next = blocks.next();
        next.getWorld().playEffect(next.getLocation().add(0.0D, 0.0D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        next.getWorld().playEffect(next.getLocation().add(-1.0D, 0.0D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        next.getWorld().playEffect(next.getLocation().add(1.0D, 0.0D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());

        next.getWorld().playEffect(next.getLocation().add(0.0D, -1.0D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        next.getWorld().playEffect(next.getLocation().add(-1.0D, -1.0D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        next.getWorld().playEffect(next.getLocation().add(1.0D, -1.0D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());

        next.getWorld().playEffect(next.getLocation().add(-1.0D, 1.0D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        next.getWorld().playEffect(next.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        next.getWorld().playEffect(next.getLocation().add(1.0D, 1.0D, 0.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());

        next.getWorld().playEffect(next.getLocation().add(0.0D, 1.0D, -1.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        next.getWorld().playEffect(next.getLocation().add(0.0D, -1.0D, -1.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        next.getWorld().playEffect(next.getLocation().add(0.0D, 0.0D, -1.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());

        next.getWorld().playEffect(next.getLocation().add(0.0D, 1.0D, 1.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        next.getWorld().playEffect(next.getLocation().add(0.0D, -1.0D, 1.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        next.getWorld().playEffect(next.getLocation().add(0.0D, 0.0D, 1.0D), Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
        if (this.timer % 3 != 0)
          return;
        try
        {
          for (Entity e : BloodWave.this.plugin.GetTargets.getTargetList(next.getLocation(), 3)) {
            if ((e instanceof LivingEntity)) {
              ((LivingEntity)e).damage(5);
            }
          }
          BloodWave.this.firework.playFirework(next.getWorld(), next.getLocation(), FireworkEffect.builder().with(FireworkEffect.Type.BURST).trail(false).flicker(true).withColor(Color.fromRGB(4194304)).withFade(Color.BLACK).build());
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    .runTaskTimer(this.plugin, 1L, 1L);
  }
}