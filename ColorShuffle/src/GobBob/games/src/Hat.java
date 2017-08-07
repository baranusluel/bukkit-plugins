package GobBob.games.src;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.EntityType;

public class Hat
{
  EntityType type;
  String customName;
  List<GameData> data = new ArrayList();

  public Hat(EntityType par1) {
    this.type = par1;
  }

  public Hat addData(GameData par1) {
    this.data.add(par1);
    return this;
  }

  public GameData getData(String par1) {
    for (int i = 0; i < this.data.size(); i++) {
      if (((GameData)this.data.get(i)).id.equalsIgnoreCase(par1)) {
        return (GameData)this.data.get(i);
      }
    }
    return null;
  }
}