/* 
 * Notes:
 *  Some of the code here has been taken from johnnywoof's AlwaysOnline plugin.
 *  
 *  I have added BungeeCord as an external jar to the build path with eclipse
 *  instead of adding it as a dependency on Maven because I needed to access
 *  some methods that were outside its API, and I didn't see BungeeCord itself
 *  on the repos, just the API and some other stuff. I've included a copy of
 *  the BungeeCord jar file I've used in the main directory of this project.
 */
package main.java.com.barancode.authbypass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.UUID;
import java.util.regex.Pattern;

import main.java.com.barancode.choiceuserdata.database.DatabaseConnection;
import main.java.com.barancode.choiceuserdata.database.DatabaseIP;
import main.java.com.barancode.choiceuserdata.database.DatabaseUUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import com.google.common.io.ByteStreams;

public class Main extends Plugin implements Listener{
	long lastCheck = 0;
	boolean isUp = true;
	HashSet<String> playersSkipped = new HashSet<String>();
	Configuration config = null;
	public static DatabaseConnection db = new DatabaseConnection();
	TaskScheduler scheduler = null;
	public static Main instance;
	{
		instance = this;
	}
	
	String INVALID_USERNAME = "Invalid username!";
	String KICK_NEW = "Please come back later. Mojang servers are down, but you're joining\n ChoiceCraft for the first time, so we have to authenticate you!";
	String KICK_IP = "Please come back later. Mojang servers are down, and your IP address isn't the same as your last one.";
	Pattern PAT = Pattern.compile("^[a-zA-Z0-9_-]{1,16}$");
	
	@Override
	public void onEnable(){
		getProxy().getPluginManager().registerListener(this, this);
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder() + "/config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        db.initialize(config.getString("host"), config.getString("username"), config.getString("password"), config.getString("database"));
        scheduler = getProxy().getScheduler();
        INVALID_USERNAME = config.getString("INVALID_USERNAME");
        KICK_NEW = config.getString("KICK_NEW");
        KICK_IP = config.getString("KICK_IP");
		getLogger().info("AuthBypass has been enabled");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLogin(final PreLoginEvent e){
		if (e.isCancelled()) return;
		e.registerIntent(this);
		
		scheduler.runAsync(this, new Runnable(){
			@Override
			public void run(){
				if ((System.currentTimeMillis() - lastCheck) / 1000 > 10){
					lastCheck = System.currentTimeMillis();
					isMojangOnline();
				}
				if (!isUp){
					String name = e.getConnection().getName();
					if (name.length() > 16 || !PAT.matcher(name).matches()){
						e.setCancelled(true);
						e.getConnection().disconnect(new ComponentBuilder(INVALID_USERNAME).create());
						return;
					}
					InitialHandler handler = (InitialHandler)e.getConnection();
					String IP = handler.getAddress().getHostString();
					String lastIP = DatabaseIP.getIPFromName(name);
					handler.setOnlineMode(false);
					if (lastIP.equals("")){
						e.setCancelled(true);
						e.getConnection().disconnect(new ComponentBuilder(KICK_NEW).create());
						return;
					}
					if (!IP.equals(lastIP)){
						e.setCancelled(true);
						e.getConnection().disconnect(new ComponentBuilder(KICK_IP).create());
						return;
					}
					ProxyServer.getInstance().getLogger().info("Mojang servers are down, skipping authentication for player " + name);
					playersSkipped.add(name);
				}
				e.completeIntent(instance);
			}
		});
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostLogin(final PostLoginEvent e){
		if (!playersSkipped.contains(e.getPlayer().getName())) return;
		playersSkipped.remove(e.getPlayer().getName());
		InitialHandler handler = (InitialHandler)e.getPlayer().getPendingConnection();
		try {
			UUID uuid = DatabaseUUID.getUUID(e.getPlayer().getName());
			Field sf = handler.getClass().getDeclaredField("uniqueId");
			sf.setAccessible(true);
			sf.set(handler, uuid);
			
			sf = handler.getClass().getDeclaredField("offlineId");
			sf.setAccessible(true);
			sf.set(handler, uuid);
		} catch (Exception ex){
			handler.setOnlineMode(true);
			e.getPlayer().disconnect(new ComponentBuilder("Please come back later. Mojang servers are down, and we can't authenticate you with our own systems").create());
			ProxyServer.getInstance().getLogger().warning("Had a problem using our own authentication systems for " + e.getPlayer().getName() + ", thus kicking them..");
			ex.printStackTrace();
		}
	}
	
	public void isMojangOnline(){
		try {
			URL obj = new URL("http://status.mojang.com/check?service=session.minecraft.net");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			String res = response.toString();
			if(res.toLowerCase().contains("red")) isUp = false;
			else isUp = true;
		} catch(IOException e){
			e.printStackTrace();
			isUp = false;
		}
	}
}
