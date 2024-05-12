package net.weesli.rvaults.database;

import net.weesli.rvaults.RVaults;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.*;

public class DataManagement {

    private static Connection connection;

    public void createConnection(){
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + RVaults.getInstance().getDatabasePath());
            setupTables();
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Connected database!");
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Database is failed, please check or report this!");
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public void setupTables() throws SQLException {
        String players = "CREATE TABLE IF NOT EXISTS players (id integer, owner string, members string, permissions string, size integer)";
        String inventory = "CREATE TABLE IF NOT EXISTS inventories (id integer, owner string, items string)";
        Statement statement = getConnection().createStatement();
        statement.executeUpdate(players);
        statement.executeUpdate(inventory);
    }
}
