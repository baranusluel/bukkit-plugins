package me.empirewand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;

public class Accelerator
{
  private Method entityGetHandle = null;
  private Method nmsEntityMove = null;
  private boolean disabled = false;

  public void accelerateEntity(Entity entity) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IllegalStateException {
    if (this.disabled) {
      return;
    }
    if (!entity.isValid()) {
      return;
    }

    if (this.entityGetHandle == null) {
      this.entityGetHandle = getMethod(entity.getClass(), "getHandle");
    }
    Object nms_entity = this.entityGetHandle.invoke(entity, null);
    if (this.nmsEntityMove == null) {
      this.nmsEntityMove = getMethod(nms_entity.getClass(), getMoveMethodeBasedOnVersion());
    }
    this.nmsEntityMove.invoke(nms_entity, null);
  }

  private Method getMethod(Class<?> cl, String method) {
    for (Method m : cl.getMethods()) {
      if (m.getName().equals(method)) {
        return m;
      }
    }
    this.disabled = true;
    throw new IllegalStateException("Unknown method: " + method + ", class: " + cl.getName());
  }

  private String getMoveMethodeBasedOnVersion() {
    String version = Bukkit.getServer().getBukkitVersion();

    if (version.startsWith("1.5")) {
      if (version.startsWith("1.5.2")) {
        return "l_";
      }
      if (version.startsWith("1.5.1")) {
        return "l_";
      }
      return "l_";
    }if (version.startsWith("1.4"))
      return "j_";
    if (version.startsWith("1.3"))
      return "h_";
    if (version.startsWith("1.2")) {
      if (version.startsWith("1.2.5")) {
        return "F_";
      }
      if (version.startsWith("1.2.3"))
        return "G_";
    } else {
      if (version.startsWith("1.1"))
        return "y_";
      if ((version.startsWith("1.0")) && 
        (version.startsWith("1.0.0"))) {
        return "w_";
      }
    }
    this.disabled = true;
    throw new IllegalStateException("Unknowns version: " + version);
  }
}