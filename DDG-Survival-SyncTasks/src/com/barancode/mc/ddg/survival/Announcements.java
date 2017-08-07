package com.barancode.mc.ddg.survival;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Announcements {
	Main plugin;
	List<String> list;
	int index = 0;
	public Announcements(Main plugin2){
		plugin = plugin2;
		list = plugin.getConfig().getStringList("announcements");
        plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run(){
				Bukkit.broadcastMessage(ChatColor.GREEN + "Melding " + ChatColor.RESET + plugin.utils.replace(list.get(index)));
				index++;
				if (index == list.size()) index = 0;
            }
        }, 0L, 120 * 20L);
	}
}
