package me.empirewand;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class Explode
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public Explode(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, Player player) {
    Block block = player.getTargetBlock(null, 50);
    final Location location = block.getLocation();
    final World world = player.getWorld();
    try {
      this.firework.playFirework(player.getWorld(), location, FireworkEffect.builder().withColor(Color.WHITE).withFade(Color.BLACK).with(FireworkEffect.Type.BURST).flicker(false).trail(false).build());
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    world.createExplosion(location, 5.0F);
    new BukkitRunnable()
    {
      public void run() {
        world.createExplosion(location, 5.0F);
      }
    }
    .runTaskTimer(this.plugin, 5L, -1L);
  }
}