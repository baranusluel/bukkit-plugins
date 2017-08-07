package main.java.com.barancode.choiceselector;

import java.io.EOFException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class Main extends JavaPlugin implements Listener, PluginMessageListener{
	ItemStack menuItem;
	Menu menu = null;
	HashMap<String, Integer> players = new HashMap<String, Integer>();
	HashMap<String, String> menuItems = new HashMap<String, String>();
	static Main instance;
	{
		instance = this;
	}
	boolean first = true;
	
	public void onEnable(){
		if (first) {
			saveDefaultConfig();
			getServer().getPluginManager().registerEvents(this, this);
			
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
				@Override
				public void run(){
					for (String s : players.keySet()){
						Utils.getPlayerCount(s);
					}
				}
			}, 0L, 5 * 20L);
			
			getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		}
		
		menuItem = new ItemStack(Material.getMaterial(getConfig().getString("item")));
		ItemMeta meta = menuItem.getItemMeta();
		meta.setDisplayName(Utils.color(getConfig().getString("item-name")));
		meta.setLore(Utils.color(getConfig().getStringList("item-lore")));
		menuItem.setItemMeta(meta);
		
		menu = new Menu(Utils.color(getConfig().getString("menu-name")), getConfig().getInt("menu-size"), new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
            	event.setWillClose(true);
            	String name = menuItems.get(event.getName());
            	Utils.connectToServer(event.getPlayer(), name);
            }
        }, this);
		
		players.clear();
		menuItems.clear();
		for (String server : getConfig().getConfigurationSection("servers").getKeys(false)){
			players.put(server, 0);
			setMenuItem(server);
		}
		
		first = false;
	}
	
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
      if (!channel.equals("BungeeCord")) {
        return;
      }
      ByteArrayDataInput in = ByteStreams.newDataInput(message);
      in.readUTF();
      String server = in.readUTF().toLowerCase();
      int playercount = in.readInt();
      players.put(server, playercount);
      setMenuItem(server);
    }
    
    public void setMenuItem(String item){
		String displayName = Utils.color(getConfig().getString("servers." + item + ".name"));
		menuItems.put(displayName, item);
		String idstring = getConfig().getString("servers." + item + ".material");
		String[] idparts = idstring.split(":");
		if (idparts.length == 1){
			menu.setOption(getConfig().getInt("servers." + item + ".slot"), new ItemStack(Material.getMaterial(idstring)),
					displayName, Utils.colorAndPlayerCount(getConfig().getStringList("servers." + item + ".lore"), players.get(item)));
		} else if (idparts.length == 2){
			menu.setOption(getConfig().getInt("servers." + item + ".slot"), new ItemStack(Material.getMaterial(idparts[0]), 1, (short)Integer.parseInt(idparts[1])),
					displayName, Utils.colorAndPlayerCount(getConfig().getStringList("servers." + item + ".lore"), players.get(item)));
		}
	}
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
    	e.getPlayer().getInventory().clear();
    	e.getPlayer().getInventory().addItem(menuItem);
		if (Bukkit.getOnlinePlayers().length == 1){
			for (String s : players.keySet()){
				Utils.getPlayerCount(s);
			}
		}
    }
    
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if (e.getAction() == Action.PHYSICAL) return;
		if (e.getItem() == null) return;
		if (e.getItem().isSimilar(menuItem)){
			menu.open(e.getPlayer());
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		if (e.getItemDrop().getItemStack().isSimilar(menuItem)) e.setCancelled(true);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e){
		if (e.getCurrentItem() != null && e.getCurrentItem().isSimilar(menuItem)) e.setCancelled(true);
		else if (e.getCursor() != null && e.getCursor().isSimilar(menuItem)) e.setCancelled(true);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		reloadConfig();
		onEnable();
		for (Player p : Bukkit.getOnlinePlayers()){
			p.getInventory().clear();
			p.getInventory().addItem(menuItem);
		}
		sender.sendMessage("Reloaded.");
		return true;
	}
}