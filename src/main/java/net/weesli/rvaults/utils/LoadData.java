package net.weesli.rvaults.utils;

import net.weesli.rvaults.RVaults;
import net.weesli.rvaults.database.DataManagement;
import net.weesli.rvaults.managers.Permissions;
import net.weesli.rvaults.managers.Storage;
import net.weesli.rvaults.managers.StorageManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class LoadData {

    static StorageManagement management = new StorageManagement();

    public static void LoadData(){
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Loading datas...");
        String sql = "SELECT * FROM players";
        try {
            Statement statement = RVaults.getInstance().getDatabase().getConnection().createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()){
                UUID uuid = UUID.fromString(result.getString("owner"));
                List<UUID> members = FormatConverter_Members(result.getString("members"));
                List<Permissions> permissions = FormatConverter_Permission(result.getString("permissions"));
                int size = result.getInt("size");
                management.getStorage().put(uuid,new Storage(uuid,members,permissions,size));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String inventory_sql = "SELECT * FROM inventories";
        try {
            Statement statement = DataManagement.getConnection().createStatement();
            ResultSet result = statement.executeQuery(inventory_sql);
            while (result.next()){
                Inventory inventory  = Bukkit.createInventory(null, management.getStorage().get(UUID.fromString(result.getString("owner"))).getSize(), ChatColor.translateAlternateColorCodes('&', RVaults.getInstance().getConfig().getString("options.menu-title").replaceAll("%player%", Bukkit.getOfflinePlayer(UUID.fromString(result.getString("owner"))).getName())));
                for (Map.Entry<Integer, ItemStack> x : FormatConverter_Inventory(result.getString("items")).entrySet()){
                    inventory.setItem(x.getKey(),x.getValue());
                }
                management.getStorage().get(UUID.fromString(result.getString("owner"))).setInventory(inventory);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<UUID> FormatConverter_Members(String value){
        String[] values = value.replace("[", "").replace("]", "").split(", ");
        List<UUID> list = new ArrayList<>();
        for (String output : values){
            if (output.isEmpty()){continue;}
            list.add(UUID.fromString(output));
        }
        return list;
    }

    public static List<Permissions> FormatConverter_Permission(String value){
        String[] values = value.replace("[", "").replace("]", "").split(", ");
        List<Permissions> list = new ArrayList<>();
        for (String output : values){
            if (output.isEmpty()){continue;}
            list.add(Permissions.valueOf(output));
        }
        return list;
    }

    public static Map<Integer, ItemStack> FormatConverter_Inventory(String value) {
        HashMap<Integer, ItemStack> itemStacks = new HashMap<>();
        String[] values = value.replace("[", "").replace("]", "").split(", ");
        for (int a = 0; values.length > a; a++) {
            String[] x = values[a].replace("{", "").replace("}", "").split("=");
            if (x[0].isEmpty() || x[1].isEmpty()){continue;}
            Integer slot = Integer.valueOf(x[0]);
            String serializedObject = x[1];
            try {
                byte[] itemBytes = Base64.getDecoder().decode(serializedObject);
                ByteArrayInputStream in = new ByteArrayInputStream(itemBytes);
                BukkitObjectInputStream is = new BukkitObjectInputStream(in);
                ItemStack itemStack = (ItemStack) is.readObject();
                itemStacks.put(slot, itemStack);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return itemStacks;
    }

}
