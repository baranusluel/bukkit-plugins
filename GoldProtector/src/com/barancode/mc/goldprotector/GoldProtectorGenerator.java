package com.barancode.mc.goldprotector;


import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
//import org.bukkit.generator.ChunkGenerator.BiomeGrid;

public class GoldProtectorGenerator extends ChunkGenerator
{
  double plotsize = 50.0D;
  double pathsize = 0.0D;
  short bottom = 20;
  short wall = 20;
  short plotfloor = 0;
  short filling = 0;
  short floor1 = 0;
  short floor2 = 0;
  int roadheight = 50;

  public short[][] generateExtBlockSections(World world, Random random, int cx, int cz, ChunkGenerator.BiomeGrid biomes)
  {
    int maxY = world.getMaxHeight();

    short[][] result = new short[maxY / 16][];

    double size = this.plotsize + this.pathsize;

    int mod2 = 0;
    int mod1 = 1;
    double n1;
    double n2;
    double n3;
    if (this.pathsize % 2.0D == 1.0D)
    {
      n1 = Math.ceil(this.pathsize / 2.0D) - 2.0D;
      n2 = Math.ceil(this.pathsize / 2.0D) - 1.0D;
      n3 = Math.ceil(this.pathsize / 2.0D);
    } else {
      n1 = Math.floor(this.pathsize / 2.0D) - 2.0D;
      n2 = Math.floor(this.pathsize / 2.0D) - 1.0D;
      n3 = Math.floor(this.pathsize / 2.0D);
    }

    if (this.pathsize % 2.0D == 1.0D)
    {
      mod2 = -1;
    }

    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        int height = this.roadheight + 2;
        for (int y = 0; y < height; y++) {
          int valx = cx * 16 + x;
          int valz = cz * 16 + z;

          setBlock(result, x, 0, z, this.bottom);
          setBlock(result, x, 50, z, this.bottom);

          if (y != 0)
          {
            if (y == this.roadheight)
            {
              if (((valx - n3 + mod1) % size == 0.0D) || ((valx + n3 + mod2) % size == 0.0D))
              {
                boolean found = false;
                for (double i = n2; i >= 0.0D; i -= 1.0D)
                {
                  if (((valz - i + mod1) % size == 0.0D) || ((valz + i + mod2) % size == 0.0D))
                  {
                    found = true;
                    break;
                  }
                }

                if (found)
                {
                  setBlock(result, x, y, z, this.floor1);
                }
                else
                {
                  setBlock(result, x, y, z, this.filling);
                }
              }
              else if (((valx - n2 + mod1) % size == 0.0D) || ((valx + n2 + mod2) % size == 0.0D))
              {
                if (((valz - n3 + mod1) % size == 0.0D) || ((valz + n3 + mod2) % size == 0.0D) || 
                  ((valz - n2 + mod1) % size == 0.0D) || ((valz + n2 + mod2) % size == 0.0D))
                {
                  setBlock(result, x, y, z, this.floor1);
                }
                else
                {
                  setBlock(result, x, y, z, this.floor2);
                }
              } else if (((valx - n1 + mod1) % size == 0.0D) || ((valx + n1 + mod2) % size == 0.0D))
              {
                if (((valz - n2 + mod1) % size == 0.0D) || ((valz + n2 + mod2) % size == 0.0D) || 
                  ((valz - n1 + mod1) % size == 0.0D) || ((valz + n1 + mod2) % size == 0.0D))
                {
                  setBlock(result, x, y, z, this.floor2);
                }
                else
                {
                  setBlock(result, x, y, z, this.floor1);
                }
              }
              else
              {
                boolean found = false;
                for (double i = n1; i >= 0.0D; i -= 1.0D)
                {
                  if (((valz - i + mod1) % size == 0.0D) || ((valz + i + mod2) % size == 0.0D))
                  {
                    found = true;
                    break;
                  }
                }

                if (found)
                {
                  setBlock(result, x, y, z, this.floor1);
                }
                else if (((valz - n2 + mod1) % size == 0.0D) || ((valz + n2 + mod2) % size == 0.0D))
                {
                  setBlock(result, x, y, z, this.floor2);
                }
                else
                {
                  boolean found2 = false;
                  for (double i = n1; i >= 0.0D; i -= 1.0D)
                  {
                    if (((valz - i + mod1) % size == 0.0D) || ((valz + i + mod2) % size == 0.0D))
                    {
                      found2 = true;
                      break;
                    }
                  }

                  if (found2)
                  {
                    setBlock(result, x, y, z, this.floor1);
                  }
                  else
                  {
                    boolean found3 = false;
                    for (double i = n3; i >= 0.0D; i -= 1.0D)
                    {
                      if (((valx - i + mod1) % size == 0.0D) || ((valx + i + mod2) % size == 0.0D))
                      {
                        found3 = true;
                        break;
                      }
                    }

                    if (found3)
                    {
                      setBlock(result, x, y, z, this.floor1);
                    }
                    else
                    {
                      setBlock(result, x, y, z, this.plotfloor);
                    }
                  }

                }

              }

            }
            else if (((valx - n3 + mod1) % size == 0.0D) || ((valx + n3 + mod2) % size == 0.0D))
            {
              boolean found = false;
              for (double i = n2; i >= 0.0D; i -= 1.0D)
              {
                if (((valz - i + mod1) % size == 0.0D) || ((valz + i + mod2) % size == 0.0D))
                {
                  found = true;
                  break;
                }
              }

              if (!found)
              {
                setBlock(result, x, y, z, this.wall);
              }
            }
            else
            {
              boolean found = false;
              for (double i = n2; i >= 0.0D; i -= 1.0D)
              {
                if (((valx - i + mod1) % size == 0.0D) || ((valx + i + mod2) % size == 0.0D))
                {
                  found = true;
                  break;
                }
              }

              if (!found)
              {
                if (((valz - n3 + mod1) % size == 0.0D) || ((valz + n3 + mod2) % size == 0.0D))
                {
                  setBlock(result, x, y, z, this.wall);
                }

              }

            }

          }

        }
      }

    }

    return result;
  }

  private void setBlock(short[][] result, int x, int y, int z, short blkid) {
    if (result[(y >> 4)] == null) {
      result[(y >> 4)] = new short[4096];
    }
    result[(y >> 4)][((y & 0xF) << 8 | z << 4 | x)] = blkid;
  }

  public Location getFixedSpawnLocation(World world, Random random)
  {
    return new Location(world, 0.0D, this.roadheight + 2, 0.0D);
  }
}