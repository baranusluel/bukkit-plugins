package me.empirewand;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class Flamethrower
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  Set<String> FlameOn = new HashSet();
  private Start plugin;

  public Flamethrower(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, final Player player) {
    if (!this.FlameOn.contains(player.getName())) {
      this.FlameOn.add(player.getName());
      new BukkitRunnable() {
        public int timer = 0;

        public void run()
        {
          if ((!Flamethrower.this.FlameOn.contains(player.getName())) || (!player.isValid()) || (player.isDead())) {
            cancel();
            Flamethrower.this.FlameOn.remove(player.getName());
          }
          if (player.getTargetBlock(null, 150).getLocation().add(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.AIR) {
            player.getTargetBlock(null, 150).getLocation().add(0.0D, 1.0D, 0.0D).getBlock().setType(Material.FIRE);
          }

          if (this.timer % 5 == 0) {
            player.playEffect(player.getLocation(), Effect.GHAST_SHOOT, 50);
          }

          if (this.timer++ > 800) {
            cancel();
            Flamethrower.this.FlameOn.remove(player.getName());
          }
        }
      }
      .runTaskTimer(this.plugin, 2L, 1L);
    } else {
      this.FlameOn.remove(player.getName());
    }
  }
}