package GobBob.games.src;

public class GameData
{
  public static final int TYPE_STRING = 0;
  public static final int TYPE_INTEGER = 1;
  public static final int TYPE_FLOAT = 2;
  public static final int TYPE_BOOLEAN = 3;
  public String id;
  public int dataType;
  String data_String;
  int data_Integer;
  float data_Float;
  boolean data_Boolean;

  public GameData(String par0, Object par1)
  {
    this.id = par0;
    if ((par1 instanceof String)) {
      this.data_String = ((String)par1);
      this.dataType = 0;
    }
    if ((par1 instanceof Integer)) {
      this.data_Integer = ((Integer)par1).intValue();
      this.dataType = 1;
    }
    if ((par1 instanceof Float)) {
      this.data_Float = ((Float)par1).floatValue();
      this.dataType = 2;
    }
    if ((par1 instanceof Boolean)) {
      this.data_Boolean = ((Boolean)par1).booleanValue();
      this.dataType = 3;
    }
  }

  public Object getData() {
    return this.dataType == 3 ? Boolean.valueOf(this.data_Boolean) : this.dataType == 2 ? Float.valueOf(this.data_Float) : this.dataType == 1 ? Integer.valueOf(this.data_Integer) : this.dataType == 0 ? this.data_String : null;
  }

  public void setData(Object par1) {
    if ((par1 instanceof String)) {
      this.data_String = ((String)par1);
      this.dataType = 0;
    }
    if ((par1 instanceof Integer)) {
      this.data_Integer = ((Integer)par1).intValue();
      this.dataType = 1;
    }
    if ((par1 instanceof Float)) {
      this.data_Float = ((Float)par1).floatValue();
      this.dataType = 2;
    }
    if ((par1 instanceof Boolean)) {
      this.data_Boolean = ((Boolean)par1).booleanValue();
      this.dataType = 3;
    }
  }
}