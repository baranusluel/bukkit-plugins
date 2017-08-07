package net.choicecraft.ChoiceWorks;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.choicecraft.ChoiceWorks.exception.PlayerNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class CListener implements Listener, PluginMessageListener{
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		final Player p = e.getPlayer();
		ChoiceWorks.scheduler.scheduleAsyncDelayedTask(ChoiceWorks.getInstance(), new Runnable(){
			@Override
			public void run(){
				try {
					// player joined the server, update userdata information:
					CUserdata.setUsername(p.getUniqueId(), p.getName());
					CUserdata.setIP(p.getUniqueId(), p.getAddress().getHostString());
				} catch (PlayerNotFoundException e) {
					// will likely never happen unless the database failed to initialize properly:
					ChoiceWorks.print("CUserdata.setIP, PlayerNotFoundException", "ChoiceWorks:onJoin");
				}
			}
		});
	}
	
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
		  return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("ChoiceWorks")) {
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			String[] incoming = null;
			try {
				incoming = msgin.readUTF().split(":");
			} catch (IOException e) {
				e.printStackTrace();
			}
			String label = incoming[0];
			String data = "";
			// This is in case : was used in the message/data
			for (int i = 1; i < incoming.length; i++){
				data += incoming[i] + ":";
			}
			data = data.substring(0, data.length() - 1);
			Map<String, String> arguments = new HashMap<String, String>();
			arguments.put("data", data);
			arguments.put("label", label);
			Bukkit.getServer().getPluginManager().callEvent(new CEvent("bungee.incoming", arguments));
		}
    }
}
