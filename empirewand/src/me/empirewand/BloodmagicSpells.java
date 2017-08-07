package me.empirewand;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum BloodmagicSpells
{
  BloodBlock, BloodWave, BloodSpark;

  private static final Map<String, BloodmagicSpells> byName = Collections.unmodifiableMap(m);

  static
  {
    BloodmagicSpells[] v = values();
    Map m = new HashMap(v.length);
    BloodmagicSpells[] arrayOfBloodmagicSpells1 = v; int j = v.length; for (int i = 0; i < j; i++) { BloodmagicSpells s = arrayOfBloodmagicSpells1[i];
      m.put(s.name().toUpperCase().replace(' ', '_'), s);
    }
  }

  public static BloodmagicSpells byId(int id)
  {
    return values()[id];
  }

  public static BloodmagicSpells byName(String name) {
    return (BloodmagicSpells)byName.get(name.toUpperCase().replace(' ', '_'));
  }

  public int getId() {
    return ordinal();
  }

  public String getName() {
    return name();
  }
}