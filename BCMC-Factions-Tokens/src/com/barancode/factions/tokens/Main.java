package com.barancode.factions.tokens;

import java.util.UUID;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.barancode.mc.db.TokenDatabase;
import com.barancode.mc.db.UUIDDatabase;

public class Main extends JavaPlugin{
	TokenDatabase td = new TokenDatabase();
	UUIDDatabase ud = new UUIDDatabase();
	
	public void onEnable(){
		setupVault();
	}
	
	  @SuppressWarnings("rawtypes")
	private void setupVault() {
		    Plugin vault = getServer().getPluginManager().getPlugin("Vault");
	
		    if (vault == null) {
		      return;
		    }
	
		    RegisteredServiceProvider economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
	
		    if (economyProvider != null) {
		      getServer().getServicesManager().unregister(economyProvider.getProvider());
		    }
	
		    getServer().getServicesManager().register(Economy.class, new Economy_FactionsTokens(this), this, ServicePriority.Highest);
	  }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0){
			sender.sendMessage(ChatColor.BLUE + "Your tokens: " + ChatColor.AQUA + td.getAmount(((Player)sender).getUniqueId()));
			sender.sendMessage(ChatColor.GREEN + "/token send <player> <amount> " + ChatColor.DARK_GREEN + "- Send someone an amount of your tokens");
		} else if (args.length == 3 && args[0].equalsIgnoreCase("send")){
			UUID uuid = ud.getUUID(args[1]);
			if (uuid == null || uuid.toString().equalsIgnoreCase("")){
				sender.sendMessage(ChatColor.RED + "A player by that name hasn't played on BCMC");
				return true;
			}
			int amount;
			try {
				amount = Integer.parseInt(args[2]);
			} catch (Exception e){
				sender.sendMessage(ChatColor.RED + "Please enter a valid amount");
				return true;
			}
			if (td.takeAmount(((Player)sender).getUniqueId(), amount)){
				td.addAmount(uuid, amount);
				sender.sendMessage(ChatColor.GOLD + "You have sent " + amount + " tokens to " + args[1]);
				if (Bukkit.getOfflinePlayer(args[1]).isOnline()) Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GOLD + sender.getName() + " has sent you " + amount + " tokens");
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You don't have that many tokens!");
				return true;
			}
		}
		return false;
	}
	
	
}
