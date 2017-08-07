package com.barancode.mc.coincommands;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Utils {
    
    public static UUID getUUID(String player) {
        String uuid = null;
        try {
            URL url = new URL("https://uuid.swordpvp.com/uuid/" + player);
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            uc.setDefaultUseCaches(false);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0");
            uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            uc.addRequestProperty("Pragma", "no-cache");
            
            Scanner scanner = new Scanner(uc.getInputStream(), "UTF-8");
            String json = scanner.useDelimiter("\\A").next();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            uuid = (String) ((JSONObject) ((JSONArray) ((JSONObject) obj).get("profiles")).get(0)).get("id");
            uuid = uuid.replaceAll(                                            
            	    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",                            
            	    "$1-$2-$3-$4-$5");  
            scanner.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return UUID.fromString(uuid);
    }
    
    public static String getName(UUID uuid) {
        String name = null;
        try {
            URL url = new URL("https://uuid.swordpvp.com/session/" + uuid.toString().replaceAll("-", ""));
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            uc.setDefaultUseCaches(false);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0");
            uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            uc.addRequestProperty("Pragma", "no-cache");
            
            Scanner scanner = new Scanner(uc.getInputStream(), "UTF-8");
            String json = scanner.useDelimiter("\\A").next();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json);
            name = (String) ((JSONObject) obj).get("name");
            scanner.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return name;
    }
}
