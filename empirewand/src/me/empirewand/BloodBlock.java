package me.empirewand;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class BloodBlock
  implements Listener
{
  FireworkEffectPlayer firework = new FireworkEffectPlayer();
  boolean alBlock = false;
  Location bl = null;
  Location bl1 = null;
  private Start plugin;

  public BloodBlock(Start instance)
  {
    this.plugin = instance;
  }

  public void castSpell(int dura, Player player) {
    if (this.alBlock) {
      this.bl1 = player.getTargetBlock(null, 50).getLocation();
      final FallingBlock block = player.getWorld().spawnFallingBlock(this.bl, Material.REDSTONE_BLOCK, (byte)0);
      block.setDropItem(false);
      block.setVelocity(new Vector(0, 2, 0));
      this.bl.getBlock().setType(Material.AIR);

      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
        public void run() {
          block.setVelocity(new Vector(0, 0, 0));
          block.setVelocity(BloodBlock.this.bl1.subtract(block.getLocation()).toVector().normalize().multiply(1));
          new BukkitRunnable() {
            public int timer = 0;

            public void run()
            {
              if (this.timer++ > 100) {
                cancel();
              }

              Location loc = this.val$block.getLocation();
              try {
                int id = 55;
                loc.getWorld().playEffect(loc, Effect.STEP_SOUND, id);
                loc.getWorld().playEffect(loc, Effect.STEP_SOUND, id);
                loc.getWorld().playEffect(loc, Effect.STEP_SOUND, id);
                loc.getWorld().playEffect(loc, Effect.STEP_SOUND, id);
                this.val$block.setDropItem(false);
              } catch (Exception e) {
                e.printStackTrace();
              }
              if (!this.val$block.isValid())
                cancel();
            }
          }
          .runTaskTimer(BloodBlock.this.plugin, 5L, 2L);
        }
      }
      , 20L);

      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
        public void run() {
          new BukkitRunnable() {
            public int timer = 0;

            public void run() {
              if (this.timer++ > 300) {
                cancel();
              }
              if ((!this.val$block.getLocation().add(0.0D, -2.0D, 0.0D).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) && (!this.val$block.getLocation().add(0.0D, -2.0D, 0.0D).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.WATER))) {
                this.val$block.setDropItem(false);
                this.val$block.getWorld().createExplosion(this.val$block.getLocation(), 5.0F);
                this.val$block.remove();
                cancel();
              }
            }
          }
          .runTaskTimer(BloodBlock.this.plugin, 1L, 1L);
        }
      }
      , 20L);
    }
    else if (!this.alBlock) {
      this.bl = player.getTargetBlock(null, 50).getLocation();
      Player p = player;
      final Block bl = p.getTargetBlock(null, 50);
      bl.setType(Material.REDSTONE_BLOCK);
      new BukkitRunnable() {
        public int timer = 0;

        public void run()
        {
          if (this.timer++ > 60) {
            cancel();
          }
          if (bl.getType() == Material.REDSTONE_BLOCK) {
            bl.getWorld().playEffect(bl.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());
          } else {
            cancel();
            BloodBlock.this.alBlock = false;
          }
        }
      }
      .runTaskTimer(this.plugin, 1L, 5L);
    }
    this.alBlock = (!this.alBlock);
  }
}