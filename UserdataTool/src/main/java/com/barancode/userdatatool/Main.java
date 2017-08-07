package main.java.com.barancode.userdatatool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.google.gson.Gson;

public class Main {
	private static final URL PROFILE_URL;
    static {
        try {
            PROFILE_URL = new URL("https://api.mojang.com/profiles/minecraft");
        } catch (MalformedURLException ex) {
            throw new IllegalStateException();
        }
    }
    private static final Gson gson = new Gson();
    static Timer timer = null;
    private static final Charset UTF8 = Charset.forName("UTF-8");
    static HashSet<String> usernameList = new HashSet<String>();
    public static File pairs = null;
    public static File usernames = null;
    public static File missingUsernames = null;
    public static File essentialsFile = null;
    
    static Runnable r = new Runnable(){
    	public void run(){
			print("Starting 10 profiles");
			
			List<String> players = new ArrayList<String>();
			int times = 0;
			for (String line : usernameList){
			   if (times == 10) break;
			   times++;
			   players.add(line);
			}
			if (times == 0){
				timer.cancel();
				print("Finished! Username and UUID pairs have been placed in pairs.txt\n==========");
				return;
			}
			
			Map<String, UUID> pairMap = null;
			try {
				pairMap = getOnlineUUIDs(players);
			} catch (IOException e) {
				print("Warning! Had a problem getting UUIDs from Mojang servers");
				e.printStackTrace();
				return;
			}
			PrintWriter out = null;
			try {
				out = new PrintWriter(new BufferedWriter(new FileWriter(pairs, true)));
			} catch (IOException e) {
				print("Failed! Had an IO Exception");
				e.printStackTrace();
				return;
			}
			for (String name : pairMap.keySet()){
				out.println(name + " " + pairMap.get(name));
			}
			out.close();
			
			for (String line : players){
				usernameList.remove(line);
			}
			
			usernames.delete();
			try {
				usernames.createNewFile();
				PrintWriter writer = new PrintWriter(usernames, "UTF-8");
				for (String s : usernameList){
					writer.println(s);
				}
				writer.close();
			} catch (IOException e) {
				print("Failed! Had an IO Exception");
				e.printStackTrace();
			}
			
			print("Processed 10 profiles");
    	}
    };

    
	public static void main(String[] args) {
		if (args.length == 0){
			print("==========\nConversion starting");
			usernames = new File("./usernames.txt");
			if (!usernames.exists()){
				print("Failed! Could not find usernames.txt");
				return;
			}
			pairs = new File("./pairs.txt");
			try {
				pairs.createNewFile();
			} catch (IOException e) {
				print("Failed! Could not create pairs.txt");
				e.printStackTrace();
				return;
			}
			try {
				task();
			} catch (FileNotFoundException e){
				print("Failed! Could not find usernames.txt");
				e.printStackTrace();
				return;
			}
		} else if (args.length == 5 && args[0].equalsIgnoreCase("database")){
			print("==========\nAttempting to upload pairs.txt to database");
			pairs = new File("./pairs.txt");
			databaseUpload(args);
		} else if (args.length == 1 && args[0].equalsIgnoreCase("verify")){
			print("==========\nVerification starting");
			usernames = new File("./usernames.txt");
			if (!usernames.exists()){
				print("Failed! Could not find usernames.txt");
				return;
			}
			pairs = new File("./pairs.txt");
			missingUsernames = new File("./missingUsernames.txt");
			try {
				missingUsernames.createNewFile();
			} catch (IOException e) {
				print("Failed! Could not create missingUsernames.txt");
				e.printStackTrace();
				return;
			}
			verify();
		} else if (args.length == 1 && args[0].equalsIgnoreCase("essentials")){
			print("==========\nConversion to Essentials csv file starting");
			pairs = new File("./pairs.txt");
			if (!pairs.exists()){
				print("Failed! Could not find pairs.txt");
				return;
			}
			essentialsFile = new File("./usermap.csv");
			essentialsConvert();
		} else {
			print("Confusing arguments O.o");
		}
	}
	
	public static void print(String s){
		System.out.println(s);
	}
	
	public static void task() throws FileNotFoundException{
		final BufferedReader br = new BufferedReader(new FileReader(usernames));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				usernameList.add(line);
			}
			br.close();
		} catch (IOException e) {
			print("Failed! Had an IO Exception");
			e.printStackTrace();
			return;
		}
		timer = new Timer();
		TimerTask task = new TimerTask(){
			public void run(){
				Thread t = new Thread(r);
				t.start();
			}
		};
		timer.scheduleAtFixedRate(task, 0, 10000);
	}
	
    private static Map<String, UUID> getOnlineUUIDs(List<String> players) throws IOException {
        Map<String, UUID> result = new HashMap<String, UUID>();
        if (!players.isEmpty()) {
            HttpURLConnection connection = (HttpURLConnection) PROFILE_URL.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json; encoding=UTF-8");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            out.write(gson.toJson(players).getBytes(UTF8));
            out.close();
            Reader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Profile[] profiles = gson.fromJson(in, Profile[].class);
            for (Profile profile : profiles) {
                result.put(profile.getName(), profile.getUUID());
            }
        }
        return result;
    }
    
    public static void databaseUpload(String[] args){
		String host = args[1];
		String database = args[2];
		String username = args[3];
		String password = (!args[4].equals("\"\"") && !args[4].equals("''")) ? args[4] : "";
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC driver missing");
			e.printStackTrace();
			return;
		}
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
		} catch (SQLException e) {
			System.out.println("MySQL connection failed!");
			e.printStackTrace();
			return;
		}
		if (con == null){
			System.out.println("MySQL connection failed!");
			return;
		}
		
		setPairs(con);
		
		try {
			if (con != null) con.close();
		} catch (SQLException e) {
			System.out.println("Could not close() the connection");
			e.printStackTrace();
		}
		
		print("\nFinished\n==========");
    }
    
	private static void setPairs(Connection con){
		HashMap<String, String> pairList = new HashMap<String, String>();
		HashMap<String, String> batch = new HashMap<String, String>();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pairs));
		} catch (FileNotFoundException e) {
			print("Failed! Could not find pairs.txt");
			e.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(" ");
				pairList.put(parts[0], parts[1]);
			}
			br.close();
		} catch (IOException e) {
			print("Failed! Had an IO Exception");
			e.printStackTrace();
			return;
		}
		
		print("Progress:");
		int numberOfStatements = (int)Math.ceil((double)pairList.size() / 15000);
		int currentStatement = 0;
		String bar = "[";
		for (int i = 0; i < numberOfStatements; i++){
			bar += " ";
		}
		bar += "]";
		System.out.print(bar);
		
		while (!pairList.isEmpty()){
            int count = 0;
            for (String username : pairList.keySet()){
            	if (count == 15000) break;
            	count++;
            	batch.put(username, pairList.get(username));
            }

			if (con != null) {
				Statement statement = null;
				try {
					statement = con.createStatement();
					String message = "INSERT IGNORE INTO players(uuid, username) VALUES";
					int firstLength = message.length();
					
					for (String name : batch.keySet()){
						if (message.length() == firstLength){
							message += "(\"" + pairList.get(name).replaceAll("-", "") + "\",\"" + name + "\")";
						} else {
							message += ",(\"" + pairList.get(name).replaceAll("-", "") + "\",\"" + name + "\")";
						}
						pairList.remove(name);
					}
					message += ";";
					
					statement.execute(message);
					
					batch.clear();
					
					currentStatement++;
					System.out.print("\r[");
					int i = 0;
					for (; i < currentStatement; i++){
						System.out.print("=");
					}
					for (; i < numberOfStatements; i++){
						System.out.print(" ");
					}
					System.out.print("]");
				} catch (Exception e){
					e.printStackTrace();
					return;
				} finally {
					try {
						if (statement != null) statement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
		        
			} else {
				print("MySQL connection failed!");
			}
		}
    }
	
	public static void verify(){
		HashMap<String, String> pairList = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(pairs));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(" ");
				pairList.put(parts[0], parts[1]);
			}
			br.close();
			br = new BufferedReader(new FileReader(usernames));
			while ((line = br.readLine()) != null) {
				usernameList.add(line);
			}
			br.close();
		} catch (Exception e){
			print("Failed! Had an exception");
			e.printStackTrace();
			return;
		}
		
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(missingUsernames, true)));
		} catch (IOException e) {
			print("Failed! Had an IO Exception");
			e.printStackTrace();
			return;
		}

		
		for (String username : usernameList){
			if (pairList.containsKey(username)){
				String uuid = pairList.get(username);
				if (uuid == null || uuid.equals("")){
					print("Found a malformed UUID! Info:");
					print("Username, " + username + "; UUID, " + uuid);
					out.println(username);
					continue;
				}
				try {
					UUID.fromString(uuid);
				} catch (Exception e){
					print("Found a malformed UUID! Info:");
					print("Username, " + username + "; UUID, " + uuid);
					out.println(username);
				}
				continue;
			} else {
				print("The player '" + username + "' is not listed in pairs.txt!");
				out.println(username);
			}
		}
		
		out.close();
	}
	
	
	public static void essentialsConvert(){		
		HashMap<String, String> pairList = new HashMap<String, String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(pairs));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(" ");
				pairList.put(parts[0], parts[1]);
			}
			br.close();
		} catch (Exception e){
			print("Failed! Had an exception");
			e.printStackTrace();
			return;
		}	
		
		try {
			essentialsFile.createNewFile();
			FileWriter writer = new FileWriter(essentialsFile);
			for (String username : pairList.keySet()){
				String uuid = pairList.get(username);
				writer.write(username + "," + uuid + "\n");
			}
		    writer.flush();
		    writer.close();
		    print("Finished!\n==========");
		} catch (IOException e) {
			print("Failed! Had an IO Exception");
		    e.printStackTrace();
		}

	}
}
