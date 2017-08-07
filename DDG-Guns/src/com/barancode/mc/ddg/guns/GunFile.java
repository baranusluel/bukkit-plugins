package com.barancode.mc.ddg.guns;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class GunFile
{
  private FileConfiguration customConfig = null;
  private File customConfigFile = null;
  Main plugin;

  public GunFile(Main plugin)
  {
    this.plugin = plugin;
  }

  public void reloadCustomConfig() {
    if (this.customConfigFile == null) {
      this.customConfigFile = new File(this.plugin.getDataFolder(), "guns.yml");
    }
    this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);

    InputStream defConfigStream = this.plugin.getResource("guns.yml");
    if (defConfigStream != null) {
      YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
      this.customConfig.setDefaults(defConfig);
    }
  }

  public FileConfiguration getCustomConfig() {
    if (this.customConfig == null) {
      reloadCustomConfig();
    }
    return this.customConfig;
  }

  public void saveCustomConfig() {
    if ((this.customConfig == null) || (this.customConfigFile == null))
      return;
    try
    {
      getCustomConfig().save(this.customConfigFile);
    } catch (IOException ex) {
      this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.customConfigFile, ex);
    }
  }

  public void saveDefaultConfig() {
    if (this.customConfigFile == null) {
      this.customConfigFile = new File(this.plugin.getDataFolder(), "guns.yml");
    }
    if (!this.customConfigFile.exists())
      this.plugin.saveResource("guns.yml", false);
  }
}