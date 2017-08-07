package com.barancode.choicefriends;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.barancode.choicefriends.database.DatabaseFriend;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

public class TabAuto implements TabExecutor{

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if (args.length == 0) return new HashSet<String>();
		Set<String> matches = new HashSet<String>();
		String search = args[args.length - 1].toLowerCase();
		for (ProxiedPlayer p : ((ProxiedPlayer)sender).getServer().getInfo().getPlayers()){
			if (p.getName().toLowerCase().startsWith(search)){
				matches.add(p.getName());
			}
		}
		for (UUID friend : DatabaseFriend.getFriends(((ProxiedPlayer)sender).getUniqueId())){
			if (Utils.isOnline(friend)){
				String friendName = Main.instance.getProxy().getPlayer(friend).getName(); 
				if (friendName.toLowerCase().startsWith(search) && !matches.contains(friendName)) matches.add("§o" + friendName + "§r");
			}
		}
		return null;
	}

}
