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
import org.bukkit.util.Vector;

public class Launch
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public Launch(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, Player player) {
    Location loc = player.getTargetBlock(null, 50).getLocation();
    try {
      this.firework.playFirework(player.getWorld(), loc, FireworkEffect.builder().withColor(Color.BLUE).withFade(Color.PURPLE).with(FireworkEffect.Type.BALL).flicker(false).trail(false).build());
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    for (final Entity entity : this.plugin.GetTargets.getTargetList(loc, 3))
      if ((entity instanceof LivingEntity)) {
        ((LivingEntity)entity).setVelocity(new Vector(0, 2, 0));
        new BukkitRunnable() {
          public int timer = 0;

          public void run()
          {
            if (entity.getLocation().add(0.0D, -1.0D, 0.0D).getBlock().getType() != Material.AIR) {
              cancel();
              return;
            }
            if (this.timer++ > 50) {
              cancel();
            }
            entity.getWorld().playEffect(entity.getLocation(), Effect.ENDER_SIGNAL, 0);
          }
        }
        .runTaskTimer(this.plugin, 6L, 3L);
      }
  }
}