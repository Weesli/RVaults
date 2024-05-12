package net.weesli.rvaults.utils;

import net.weesli.rvaults.RVaults;
import net.weesli.rvaults.database.DataManagement;
import net.weesli.rvaults.managers.Storage;
import net.weesli.rvaults.managers.StorageManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class SaveData {

    StorageManagement management = new StorageManagement();

    public SaveData(){
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Saving datas...");
        String updater_sql = "UPDATE players SET permissions=? , members=?, size=?";
        String inventory_sql = "UPDATE inventories SET items=?";
        for (Map.Entry<UUID, Storage> values : management.getStorage().entrySet()){
            try {
                PreparedStatement statement = DataManagement.getConnection().prepareStatement(updater_sql);
                statement.setString(1, values.getValue().getPermissions().toString());
                statement.setString(2, values.getValue().getMembers().toString());
                statement.setInt(3, values.getValue().getSize());
                statement.executeUpdate();
                PreparedStatement inventory_statement = DataManagement.getConnection().prepareStatement(inventory_sql);
                int a = 0;
                List<Map<Integer, String>> itemList = new ArrayList<>();
                for (ItemStack itemStack : values.getValue().getInventory().getContents()){
                    if (itemStack == null){a++; continue;}
                    try {
                        ByteArrayOutputStream io = new ByteArrayOutputStream();
                        BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
                        os.writeObject(itemStack);
                        os.flush();
                        byte[] serialized = io.toByteArray();
                        itemList.add(Map.of(a, Base64.getEncoder().encodeToString(serialized)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    a++;
                }
                inventory_statement.setString(1,itemList.toString());
                inventory_statement.executeUpdate();
                inventory_statement.close();
                statement.close();
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(values.getKey() + " is not saved!");
            }
        }
    }
}
