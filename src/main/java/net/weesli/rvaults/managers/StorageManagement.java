package net.weesli.rvaults.managers;

import net.weesli.rvaults.RVaults;
import net.weesli.rvaults.database.DataManagement;
import net.weesli.rvaults.database.messagesFile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class StorageManagement {

    private static HashMap<UUID, Storage> storage = new HashMap<>();

    public void createStorage(Player player, int size){
        if (checkStorage(player)){
            return;
        }
        getStorage().put(player.getUniqueId(), new Storage(
                player.getUniqueId(),
                new ArrayList<>(),
                new ArrayList<>(),
                size
        ));
        InsertData(player);
    }


    public boolean checkStorage(Player player){
        return getStorage().containsKey(player.getUniqueId());
    }

    public HashMap<UUID, Storage> getStorage(){
        return storage;
    }

    public Storage getPlayerStorage(Player player){
        if (!checkStorage(player)){
            createStorage(player,getStarterSize());
        }
        return getStorage().get(player.getUniqueId());
    }

    public boolean checkPermission(Player player, Permissions permissions){
        if (!checkStorage(player)){
            return false;
        }
        return getPlayerStorage(player).getPermissions().contains(permissions);
    }

    public void openStorage(Player player) {
        if (getPlayerStorage(player) == null){
            createStorage(player,getStarterSize());
        }
        int max_size = getStarterSize() / 9;
        for (PermissionAttachmentInfo ignored : player.getEffectivePermissions()){
            String[] x = ignored.getPermission().split("[.]");
            String permission =x[0] + "." + x[1];
            if (permission.equalsIgnoreCase("rvaults.size")){
                if (max_size < Integer.parseInt(x[2])){
                    max_size = Integer.parseInt(x[2]);
                }
            }
        }
        if (max_size != getStarterSize()){
            getPlayerStorage(player).setSize(max_size * 9);
        }
        player.openInventory(getPlayerStorage(player).getInventory());
    }

    public int getStarterSize(){
        return RVaults.getInstance().getConfig().getInt("options.starter-size") * 9;
    }

    public void InsertData(Player player){
        String sql = "INSERT INTO players (owner, members, permissions, size) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement player_stmt = DataManagement.getConnection().prepareStatement(sql);
            player_stmt.setString(1, String.valueOf(player.getUniqueId()));
            player_stmt.setString(2, getStorage().get(player.getUniqueId()).getMembers().toString());
            player_stmt.setString(3, getStorage().get(player.getUniqueId()).getPermissions().toString());
            player_stmt.setInt(4, getStorage().get(player.getUniqueId()).getSize());
            player_stmt.executeUpdate();
            player_stmt.close();
            PreparedStatement inventory_stmt = DataManagement.getConnection().prepareStatement("INSERT INTO inventories (owner, items) VALUES (?, ?)");
            inventory_stmt.setString(1, String.valueOf(player.getUniqueId()));
            inventory_stmt.setString(2, new ArrayList<>().toString());
            inventory_stmt.executeUpdate();
            inventory_stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void DeleteData(UUID uuid){
        String sql = "DELETE FROM players WHERE owner=?";
        String sql1 = "DELETE FROM inventories WHERE owner=?";
        try {
            PreparedStatement statement = DataManagement.getConnection().prepareStatement(sql);
            statement.setString(1, String.valueOf(uuid));
            statement.executeUpdate();
            PreparedStatement statement1 = DataManagement.getConnection().prepareStatement(sql1);
            statement1.setString(1, String.valueOf(uuid));
            statement1.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMessage(String path){
        return ChatColor.translateAlternateColorCodes('&', RVaults.getInstance().getConfig().getString("options.prefix")) + ChatColor.translateAlternateColorCodes('&', messagesFile.getFile().getString(path));
    }

    public boolean CheckMemberinPlayer(UUID target, UUID player){
        if (getStorage().get(target) == null){
            return false;
        }
        if (getStorage().get(target).getMembers().contains(player)){
            return true;
        }
        return false;
    }


}
