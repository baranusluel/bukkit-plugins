package GobBob.games.src.mysql;

import GobBob.games.src.GamePlayer;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect
{
  private Connection con;
  private Statement st;
  private ResultSet rs;

  public DBConnect()
  {
    try
    {
      Class.forName("com.mysql.jdbc.Driver");
      this.con = DriverManager.getConnection("jdbc:mysql://gator3048.hostgator.com/harmiox_tokens", "harmiox_server", "minetoken2014");
      this.st = this.con.createStatement();
    }
    catch (Exception ex) {
      System.out.println("Error: " + ex);
    }
  }

  public void getData() {
    try {
      this.con = DriverManager.getConnection("jdbc:mysql://gator3048.hostgator.com/harmiox_tokens", "harmiox_server", "minetoken2014");
      this.st = this.con.createStatement();
      String query = "select * from tokens";
      this.rs = this.st.executeQuery(query);
      System.out.println("Loading from Database");
      while (this.rs.next()) {
        String name = this.rs.getString("username");
        int age = this.rs.getInt("tokens");
        System.out.println("Name: " + name + "   Age: " + age);
      }
    } catch (Exception ex) {
      System.out.println(ex);
    }
  }

  public void saveData() {
  }

  public boolean loadPlayerData(GamePlayer player, String par1) {
    boolean isNew = true;
    try {
      this.con = DriverManager.getConnection("jdbc:mysql://gator3048.hostgator.com/harmiox_tokens", "harmiox_server", "minetoken2014");
      this.st = this.con.createStatement();
      String query = "SELECT * from tokens WHERE username = '" + par1 + "'";
      this.rs = this.st.executeQuery(query);
      System.out.println("Loading Player " + par1 + " from Database");
      while (this.rs.next()) {
        int tokens = this.rs.getInt("tokens");
        System.out.println("Tokens: " + tokens);
        player.tokens = tokens;
        isNew = false;
      }
    } catch (Exception ex) {
      System.out.println(ex);
    }
    return isNew;
  }

  public void savePlayerData(GamePlayer player) {
    try {
      this.con = DriverManager.getConnection("jdbc:mysql://gator3048.hostgator.com/harmiox_tokens", "harmiox_server", "minetoken2014");
      this.st = this.con.createStatement();
      System.out.println("Saving " + player.name + " to Database");
      String query = "UPDATE tokens SET tokens='" + player.tokens + "' WHERE username='" + player.name + "'";
      this.st.executeUpdate(query);
    } catch (Exception ex) {
      System.out.println(ex);
    }
  }

  public boolean doesPlayerExist(String par1) {
    String queryCheck = "SELECT * from tokens WHERE username = '" + par1 + "'";
    try {
      this.rs = this.st.executeQuery(queryCheck);
      if (this.rs.next())
      {
        return true;
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public void createNewPlayerInDatabase(String name) {
    String query = "INSERT INTO tokens (username,tokens) VALUES ('" + name + "','0')";
    try {
      PreparedStatement preparedStmt = this.con.prepareStatement(query);
      preparedStmt.execute();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }
}