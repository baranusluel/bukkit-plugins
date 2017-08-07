package me.empirewand;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class GetTargets
{
  private Start plugin;

  public GetTargets(Start instance)
  {
    this.plugin = instance;
  }

  public List<Entity> getTargetList(Location loc, int radius) {
    List target = new ArrayList();
    int rs = radius * radius;
    Location tmp = new Location(loc.getWorld(), 0.0D, 0.0D, 0.0D);
    for (Entity entity : loc.getWorld().getEntities()) {
      if (entity.getLocation(tmp).distanceSquared(loc) < rs) {
        target.add(entity);
      }
    }
    return target;
  }
}