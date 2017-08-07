package me.empirewand;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class ExplosionWave
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  private Start plugin;

  public ExplosionWave(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, Player player) {
    final BlockIterator blocknext = new BlockIterator(player);
    blocknext.next();
    blocknext.next();
    blocknext.next();
    blocknext.next();
    blocknext.next();
    blocknext.next();
    blocknext.next();
    blocknext.next();
    blocknext.next();
    blocknext.next();
    new BukkitRunnable() {
      public int timer = 0;

      public void run() {
        if (this.timer++ > 40) {
          cancel();
        }
        if (!blocknext.hasNext()) {
          cancel();
        }
        Block next = blocknext.next();

        if (this.timer % 2 != 0)
          return;
        try
        {
          next.getWorld().createExplosion(next.getLocation(), 2.0F);
          Location loc = next.getLocation();
          int radiusEffect = 1;
          int X = loc.getBlockX();
          int Y = loc.getBlockY();
          int Z = loc.getBlockZ();
          int startX = X - radiusEffect;
          int startY = Y - radiusEffect;
          int startZ = Z - radiusEffect;
          int endX = X + radiusEffect;
          int endY = Y + radiusEffect;
          int endZ = Z + radiusEffect;
          World world = loc.getWorld();

          for (int cX = startX; cX <= endX; cX++) {
            for (int cY = startY; cY <= endY; cY++)
              for (int cZ = startZ; cZ <= endZ; cZ++)
                if ((cX - X ^ 2 + (cY - Y) ^ 2 + (cZ - Z) ^ 0x2) <= 4)
                {
                  Block block = world.getBlockAt(cX, cY, cZ);
                  world.playEffect(block.getLocation(), Effect.SMOKE, BlockFace.UP);
                }
          }
        }
        catch (IllegalArgumentException e)
        {
          e.printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    .runTaskTimer(this.plugin, 1L, 1L);
  }
}