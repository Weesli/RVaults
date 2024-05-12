package net.weesli.rvaults;

import net.milkbowl.vault.permission.Permission;
import net.weesli.rvaults.Commands.StorageCommands;
import net.weesli.rvaults.UI.UIListener;
import net.weesli.rvaults.database.DataManagement;
import net.weesli.rvaults.database.messagesFile;
import net.weesli.rvaults.utils.LoadData;
import net.weesli.rvaults.utils.SaveData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class RVaults extends JavaPlugin {

    private static RVaults instance;
    private static Permission perms = null;
    private DataManagement database = new
            DataManagement();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        messagesFile.setupFile();
        messagesFile.getFile().options().copyDefaults(true);
        messagesFile.saveFile();
        setupDB();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-------------------------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "RVaults - Author by Weesli");
        database.createConnection();
        LoadData.LoadData();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-------------------------------------");
        this.getServer().getPluginManager().registerEvents(new UIListener(),this);
        // Commands setup
        getCommand("storage").setExecutor(new StorageCommands());
        getCommand("storage").setTabCompleter(new StorageCommands());
        getCommand("rstorage").setExecutor(new StorageCommands());
        getCommand("rstorage").setTabCompleter(new StorageCommands());
        // Vault permission setups
        setupPermissions();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-------------------------------------");
        new SaveData();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-------------------------------------");
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static RVaults getInstance(){
        return instance;
    }


    private void setupDB(){
        File file = new File(getDataFolder(), "data.db");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Data file is not setup!");
            }
        }
    }

    public String getDatabasePath(){
        return new File(getDataFolder(), "data.db").getPath();
    }

    public DataManagement getDatabase(){
        return database;
    }
    public Permission getPerms(){
        return perms;
    }
}
