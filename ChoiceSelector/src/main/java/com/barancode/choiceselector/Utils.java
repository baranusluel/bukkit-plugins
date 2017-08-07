package main.java.com.barancode.choiceselector;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;

public class Utils {
	public static String color(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	public static List<String> color(List<String> old){
		List<String> l = new ArrayList<String>();
		for (String s : old){
			l.add(color(s));
		}
		return l;
	}
	public static String[] colorAndPlayerCount(List<String> old, int count){
		String[] a = new String[old.size()];
		int i = 0;
		for (String s : old){
			a[i] = color(s).replaceAll("<count>", count + "");
			i++;
		}
		return a;
	}
    public static boolean connectToServer(Player p, String server){
        try{
          Messenger messenger = Bukkit.getMessenger();
          
          if (!messenger.isOutgoingChannelRegistered(Main.instance, "BungeeCord")) {
            messenger.registerOutgoingPluginChannel(Main.instance, "BungeeCord");
          }

          ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
          
          DataOutputStream out = new DataOutputStream(byteArray);

          out.writeUTF("Connect"); out.writeUTF(server);

          p.sendPluginMessage(Main.instance, "BungeeCord", byteArray.toByteArray());
        }
        catch (Exception ex) {
          ex.printStackTrace();
          return false;
        }

        return true;
      }
    public static boolean getPlayerCount(String server){
        try{
          Messenger messenger = Bukkit.getMessenger();
          
          if (!messenger.isOutgoingChannelRegistered(Main.instance, "BungeeCord")) {
            messenger.registerOutgoingPluginChannel(Main.instance, "BungeeCord");
          }

          ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
          
          DataOutputStream out = new DataOutputStream(byteArray);

          out.writeUTF("PlayerCount");
          out.writeUTF(server);

          if (Bukkit.getOnlinePlayers().length > 0) Bukkit.getOnlinePlayers()[0].sendPluginMessage(Main.instance, "BungeeCord", byteArray.toByteArray());
        }
        catch (Exception ex) {
          ex.printStackTrace();
          return false;
        }

        return true;
      }
}
