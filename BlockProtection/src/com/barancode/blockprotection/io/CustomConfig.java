package com.barancode.blockprotection.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.barancode.blockprotection.Main;

public class CustomConfig
{
  private FileConfiguration customConfig = null;
  private File customConfigFile = null;
  Main plugin;
  String name = "custom.yml";

  public CustomConfig(Main plugin, String name)
  {
    this.plugin = plugin;
    this.name = name;
  }

  @SuppressWarnings("deprecation")
  public void reloadCustomConfig() {
	if (customConfigFile == null) {
      customConfigFile = new File(plugin.getDataFolder(), name);
    }
    customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);

    InputStream defConfigStream = plugin.getResource(name);
    if (defConfigStream != null) {
      YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
      customConfig.setDefaults(defConfig);
    }
  }

  public FileConfiguration getCustomConfig() {
    if (customConfig == null) {
      reloadCustomConfig();
    }
    return customConfig;
  }

  public void saveCustomConfig() {
    if ((customConfig == null) || (customConfigFile == null)) return;
    try
    {
      getCustomConfig().save(customConfigFile);
    } catch (IOException ex) {
      plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
    }
  }

  public void saveDefaultConfig() {
    if (customConfigFile == null) {
    	File folder = plugin.getDataFolder();
    	if (!folder.exists()) folder.mkdir();
    	customConfigFile = new File(plugin.getDataFolder(), name);
    }
    if (!customConfigFile.exists()) plugin.saveResource(name, false);
  }
}