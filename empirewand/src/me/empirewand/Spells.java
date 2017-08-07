package me.empirewand;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Spells
{
  Little_Spark, Spark, LightningArrow, PoisonWave, ExplosionWave, FlameWave, Comet, Explosive, Leap, Confuse, Flamethrower, Capture, Launch, EnderEgg, Explode, Lightningstorm, Escape;

  private static final Map<String, Spells> byName = new HashMap<String, Spells>();

  static
  {
    Spells[] v = values();
    Map m = new HashMap(v.length);
    Spells[] arrayOfSpells1 = v; int j = v.length; for (int i = 0; i < j; i++) { Spells s = arrayOfSpells1[i];
      m.put(s.name().toUpperCase().replace(' ', '_'), s);
    }
  }

  public static Spells byId(int id)
  {
    return values()[id];
  }

  public static Spells byName(String name) {
    return (Spells)byName.get(name.toUpperCase().replace(' ', '_'));
  }

  public int getId() {
    return ordinal();
  }

  public String getName() {
    return name();
  }
}