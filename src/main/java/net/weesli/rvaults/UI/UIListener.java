package net.weesli.rvaults.UI;

import net.weesli.rvaults.RVaults;
import net.weesli.rvaults.managers.Permissions;
import net.weesli.rvaults.managers.Storage;
import net.weesli.rvaults.managers.StorageManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.UUID;

public class UIListener implements Listener {

    StorageManagement management = new StorageManagement();


    @EventHandler
    public void saveInventory(InventoryCloseEvent e){
        Player player = (Player) e.getPlayer();
        boolean isStorage = false;
        UUID Storage_owner = player.getUniqueId();
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', RVaults.getInstance().getConfig().getString("options.menu-title").replaceAll("%player%", player.getName())))){
            isStorage = true;
            Storage_owner = player.getUniqueId();
        }else{
            for (OfflinePlayer target : Bukkit.getOfflinePlayers()){
                String player_name = target.getName();
                if (e.getView().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', RVaults.getInstance().getConfig().getString("options.menu-title").replaceAll("%player%", player_name)))){
                    isStorage = true;
                    Storage_owner = target.getUniqueId();
                    break;
                }
            }
        }
        if (isStorage){
            if (management.getStorage().get(Storage_owner) != null){
                management.getStorage().get(Storage_owner).setInventory(e.getView().getTopInventory());
            }
        }
    }

    @EventHandler
    public void ChangeEvent(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null){return;}
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', RVaults.getInstance().getConfig().getString("options.menu-title").replaceAll("%player%", player.getName())))){
            return;
        }else{
            for (OfflinePlayer target : Bukkit.getOfflinePlayers()){
                String player_name = target.getName();
                if (e.getView().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', RVaults.getInstance().getConfig().getString("options.menu-title").replaceAll("%player%", player_name)))){
                    Storage storage = management.getStorage().get(target.getUniqueId());
                    if (storage != null){
                        if (storage.getPermissions().contains(Permissions.PICK_ITEM)){
                            if (e.getClickedInventory() == e.getView().getTopInventory()){
                                e.setCancelled(true);
                                player.sendMessage(management.getMessage("PICK-PERMISSION"));
                            }
                        }
                        if (storage.getPermissions().contains(Permissions.PUT_ITEM)){
                            if (e.getClickedInventory() == e.getView().getBottomInventory()){
                                e.setCancelled(true);
                                player.sendMessage(management.getMessage("PUT-PERMISSION"));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void PermissionUIEvent(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null){return;}
        if (!e.getView().getTitle().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', RVaults.getInstance().getConfig().getString("inventories.permission-menu.title")))){return;}
        e.setCancelled(true);
        int slot = e.getSlot();
        BaseUI ui = new BaseUI();
        int pick_item_slot = ui.getSlot("pick");
        int put_item_slot = ui.getSlot("put");
        int open_item_slot = ui.getSlot("open");
        if (slot == pick_item_slot) {
            if (management.checkPermission(player, Permissions.PICK_ITEM)){
                management.getPlayerStorage(player).removePermission(Permissions.PICK_ITEM);
            }else {
                management.getPlayerStorage(player).addPermission(Permissions.PICK_ITEM);
            }
            ui.openPermissionMenu(player);
        }
        if (slot == put_item_slot){
            if (management.checkPermission(player, Permissions.PUT_ITEM)){
                management.getPlayerStorage(player).removePermission(Permissions.PUT_ITEM);
            }else {
                management.getPlayerStorage(player).addPermission(Permissions.PUT_ITEM);
            }
            ui.openPermissionMenu(player);
        }
        if (open_item_slot == slot){
            if (management.checkPermission(player, Permissions.OPEN)){
                management.getPlayerStorage(player).removePermission(Permissions.OPEN);
            }else {
                management.getPlayerStorage(player).addPermission(Permissions.OPEN);
            }
            ui.openPermissionMenu(player);
        }
    }


}
