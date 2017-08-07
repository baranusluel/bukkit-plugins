package com.barancode.factions.tokens;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Economy_FactionsTokens implements Economy {
	Main plugin;
	
	public Economy_FactionsTokens(Main plugin){
		this.plugin = plugin;
	}
	
	
	  public boolean isEnabled()
	  {
	    return plugin != null;
	  }
	
	  public String format(double amount)
	  {
	    return (int)amount + "";
	  }
	
	  public String currencyNameSingular()
	  {
	    return "token";
	  }
	
	  public String currencyNamePlural()
	  {
	    return "tokens";
	  }
	
	  public double getBalance(String playerName)
	  {
	    UUID id = plugin.ud.getUUID(playerName);
	    if (id == null) return 0.0D;
	    return plugin.td.getAmount(id);
	  }
	
	  public EconomyResponse withdrawPlayer(String playerName, double amount)
	  {
	    if (amount < 0.0D) {
	      return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
	    }
	    
	    UUID id = plugin.ud.getUUID(playerName);
	    if (id == null) {
	      return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Account doesn't exist");
	    }

	    if (plugin.td.takeAmount(id, (int)amount)) {
	      return new EconomyResponse(amount, plugin.td.getAmount(id), EconomyResponse.ResponseType.SUCCESS, "");
	    }
	    return new EconomyResponse(0.0D, plugin.td.getAmount(id), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
	  }
	
	  public EconomyResponse depositPlayer(String playerName, double amount)
	  {
	    if (amount < 0.0D) {
	      return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
	    }
	    
	    UUID id = plugin.ud.getUUID(playerName);
	    if (id == null) {
	      return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.FAILURE, "Account doesn't exist");
	    }
	    
	    plugin.td.addAmount(id, (int)amount);

	    return new EconomyResponse(amount, plugin.td.getAmount(id), EconomyResponse.ResponseType.SUCCESS, "");
	  }
	
	  public boolean has(String playerName, double amount)
	  {
	    return getBalance(playerName) >= amount;
	  }
	
	  public EconomyResponse createBank(String name, String player)
	  {
	    return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	  }
	
	  public EconomyResponse deleteBank(String name)
	  {
	    return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	  }
	
	  public EconomyResponse bankHas(String name, double amount)
	  {
	    return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	  }
	
	  public EconomyResponse bankWithdraw(String name, double amount)
	  {
	    return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	  }
	
	  public EconomyResponse bankDeposit(String name, double amount)
	  {
	    return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	  }
	
	  public EconomyResponse isBankOwner(String name, String playerName)
	  {
	    return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	  }
	
	  public EconomyResponse isBankMember(String name, String playerName)
	  {
	    return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	  }
	
	  public EconomyResponse bankBalance(String name)
	  {
	    return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	  }
	
	  public List<String> getBanks()
	  {
	    return new ArrayList<String>();
	  }
	
	  public boolean hasBankSupport()
	  {
	    return false;
	  }
	
	  public boolean hasAccount(String playerName)
	  {
	    return plugin.ud.getUUID(playerName) != null;
	  }
	
	  public boolean createPlayerAccount(String playerName)
	  {
	    return false;
	  }
	
	  public int fractionalDigits()
	  {
	    return -1;
	  }
	
	  public boolean hasAccount(String playerName, String worldName)
	  {
	    return hasAccount(playerName);
	  }
	
	  public double getBalance(String playerName, String world)
	  {
	    return getBalance(playerName);
	  }
	
	  public boolean has(String playerName, String worldName, double amount)
	  {
	    return has(playerName, amount);
	  }
	
	  public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount)
	  {
	    return withdrawPlayer(playerName, amount);
	  }
	
	  public EconomyResponse depositPlayer(String playerName, String worldName, double amount)
	  {
	    return depositPlayer(playerName, amount);
	  }
	
	  public boolean createPlayerAccount(String playerName, String worldName)
	  {
	    return createPlayerAccount(playerName);
	  }

	
	public EconomyResponse createBank(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	}

	
	public boolean createPlayerAccount(OfflinePlayer arg0) {
		return false;
	}

	
	public boolean createPlayerAccount(OfflinePlayer arg0, String arg1) {
		return false;
	}

	
	public EconomyResponse depositPlayer(OfflinePlayer arg0, double arg1) {
		return depositPlayer(arg0.getName(), arg1);
	}

	
	public EconomyResponse depositPlayer(OfflinePlayer arg0, String arg1, double arg2) {
		return depositPlayer(arg0.getName(), arg2);
	}

	
	public double getBalance(OfflinePlayer arg0) {
		return getBalance(arg0.getName());
	}

	
	public double getBalance(OfflinePlayer arg0, String arg1) {
		return getBalance(arg0.getName());
	}

	
	public boolean has(OfflinePlayer arg0, double arg1) {
		return has(arg0.getName(), arg1);
	}

	
	public boolean has(OfflinePlayer arg0, String arg1, double arg2) {
		return has(arg0.getName(), arg2);
	}

	
	public boolean hasAccount(OfflinePlayer arg0) {
		return hasAccount(arg0.getName());
	}

	
	public boolean hasAccount(OfflinePlayer arg0, String arg1) {
		return hasAccount(arg0.getName());
	}

	
	public EconomyResponse isBankMember(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	}

	
	public EconomyResponse isBankOwner(String arg0, OfflinePlayer arg1) {
		return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Does not support bank accounts!");
	}

	
	public EconomyResponse withdrawPlayer(OfflinePlayer arg0, double arg1) {
		return withdrawPlayer(arg0.getName(), arg1);
	}

	
	public EconomyResponse withdrawPlayer(OfflinePlayer arg0, String arg1, double arg2) {
		return withdrawPlayer(arg0.getName(), arg2);
	}

	
	public String getName() {
		return "FactionsTokens";
	}
}
