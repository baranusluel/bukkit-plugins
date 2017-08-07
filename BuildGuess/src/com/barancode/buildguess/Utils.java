package com.barancode.buildguess;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.util.Vector;

public class Utils {
	public static String color(String s){
		return ChatColor.translateAlternateColorCodes('&', s).replaceAll("&&", "\n");
	}
	public static List<String> color(List<String> list){
		List<String> newlist = new ArrayList<String>();
		for (String s : list){
			newlist.add(color(s));
		}
		return newlist;
	}
	public static Block getTargetBlock(Player player, int range) {
		Location loc = player.getEyeLocation();
		Vector dir = loc.getDirection().normalize();
		for (int i = 0; i <= range; i++) {
			Block b = loc.add(dir).getBlock();
			if (b != null && b.getType() != Material.AIR) return b;
		}
		return null;
	}
	public static List<String> toLowercase(List<String> list){
		List<String> newlist = new ArrayList<String>();
		for (String s : list){
			newlist.add(s.toLowerCase());
		}
		return newlist;
	}
    public static boolean connectToHub(Player player, Main plugin)
    {
      try
      {
        Messenger messenger = Bukkit.getMessenger();
        
        if (!messenger.isOutgoingChannelRegistered(plugin, "BungeeCord")) {
          messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
        }

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        
        DataOutputStream out = new DataOutputStream(byteArray);

        out.writeUTF("Connect"); out.writeUTF("hub");

        player.sendPluginMessage(plugin, "BungeeCord", byteArray.toByteArray());
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }

      return true;
    }
}
