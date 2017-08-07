package me.empirewand;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

class EnderEgg
{
  BukkitRunnable task = new BukkitRunnable()
  {
    public void run() {
      if (!EnderEgg.this.onTick())
        cancel();
    }
  };

  Listener list = new Listener() {
    @EventHandler
    public void onEvent(EntityChangeBlockEvent evt) {
      if (evt.getEntity() == EnderEgg.this.bl) {
        evt.setCancelled(true);
      }
      if (!EnderEgg.this.bl.isValid())
        EntityChangeBlockEvent.getHandlerList().unregister(this);
    }
  };
  private final FallingBlock bl;
  private final Location target;
  private final int maxTimer;
  private int timer;
  private final Handler handler;
  private static final Vector DIVIDER = new Vector(1.5D, 1.5D, 1.5D);

  public EnderEgg(FallingBlock start, Location target, int maxTimer) {
    this(start, target, maxTimer, null);
  }

  public EnderEgg(FallingBlock start, Location target, int maxTimer, Handler handler)
  {
    this.bl = start;

    this.target = target;
    this.maxTimer = maxTimer;
    this.handler = handler;
  }

  public void start(Plugin pl)
  {
    this.task.runTaskTimer(pl, 1L, 1L);
    this.bl.setDropItem(false);
    pl.getServer().getPluginManager().registerEvents(this.list, pl);
  }

  private boolean onTick() {
    if (this.handler != null) {
      this.handler.onTick(this.bl, this.target);
    }
    if (this.timer++ > this.maxTimer) {
      this.bl.remove();
      return false;
    }
    if (!this.bl.isValid()) {
      return false;
    }
    Vector v = this.target.toVector().subtract(this.bl.getLocation().toVector());
    v.multiply(0.5D);
    if (v.lengthSquared() > 1.0D) {
      v.normalize();
    }
    v.add(this.bl.getVelocity().multiply(0.9D));
    v.divide(DIVIDER);
    this.bl.setVelocity(v);
    return true;
  }

  public static abstract interface Handler
  {
    public abstract void onTick(FallingBlock paramFallingBlock, Location paramLocation);
  }
}