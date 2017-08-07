package com.barancode.joinfull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

public class Main extends JavaPlugin implements Listener{
	Random r = new Random();
	public void onEnable(){
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
	}
	 @EventHandler(priority = EventPriority.HIGHEST)
	public void preJoin(AsyncPlayerPreLoginEvent e){
		 if(e.getLoginResult() == Result.KICK_FULL){
			 e.setLoginResult(Result.ALLOWED);
		 }
	}
	 @EventHandler(priority = EventPriority.HIGHEST)
	 public void onJoin(PlayerJoinEvent e){
		if(Bukkit.getOnlinePlayers().length <= Bukkit.getMaxPlayers()) return;
		Player p = e.getPlayer();
		if(!p.hasPermission("bcmc.donator.join")){
			p.sendMessage(ChatColor.RED + "BCMC > The server is full.");
			connectToHub(p);
		}else{
			Player randomPlayer = null;
			int error = 50;
			do {
				error--;
				if (error == 0){
					p.kickPlayer(ChatColor.RED + "An error ocurred");
					return;
				}
				randomPlayer = Bukkit.getOnlinePlayers()[r.nextInt(Bukkit.getOnlinePlayers().length)];
			} while (randomPlayer.hasPermission("bcmc.donator.join"));
			
			randomPlayer.sendMessage(ChatColor.RED + "BCMC > You have been kicked to make space for a donator.\n" + ChatColor.GOLD + "Want to make sure you don't get kicked? Donate at http://bcmcnetwork.com/shop.php");
			connectToHub(randomPlayer);
		}
	 }
	
    public boolean connectToHub(Player player)
    {
      try
      {
        Messenger messenger = Bukkit.getMessenger();
        
        if (!messenger.isOutgoingChannelRegistered(this, "BungeeCord")) {
          messenger.registerOutgoingPluginChannel(this, "BungeeCord");
        }

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        
        DataOutputStream out = new DataOutputStream(byteArray);

        out.writeUTF("Connect"); out.writeUTF("Hub");

        player.sendPluginMessage(this, "BungeeCord", byteArray.toByteArray());
      }
      catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }

      return true;
    }
}
