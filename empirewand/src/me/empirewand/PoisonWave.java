package me.empirewand;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class PoisonWave
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public PoisonWave(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, final Player player) {
    final BlockIterator blockNext = new BlockIterator(player);
    new BukkitRunnable() {
      public int timer = 0;

      public void run() {
        if (this.timer++ > 50) {
          cancel();
        }
        if (!blockNext.hasNext()) {
          cancel();
        }
        Block next = blockNext.next();
        try {
          for (Entity entity : PoisonWave.this.plugin.GetTargets.getTargetList(next.getLocation(), 3)) {
            if ((entity instanceof LivingEntity)) {
              ((LivingEntity)entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 1));
            }
          }
          player.removePotionEffect(PotionEffectType.POISON);
          PoisonWave.this.firework.playFirework(next.getWorld(), next.getLocation(), FireworkEffect.builder().with(FireworkEffect.Type.BURST).trail(true).flicker(true).withColor(Color.fromRGB(15370)).withFade(Color.BLACK).build());
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