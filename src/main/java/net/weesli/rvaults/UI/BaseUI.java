package net.weesli.rvaults.UI;

import net.weesli.rvaults.RVaults;
import net.weesli.rvaults.managers.Permissions;
import net.weesli.rvaults.managers.StorageManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BaseUI {

    static RVaults main = RVaults.getInstance();
    static StorageManagement management = new StorageManagement();

    public static void openPermissionMenu(Player player){
        if (!management.checkStorage(player)){
            return;
        }
        Inventory inventory = Bukkit.createInventory(null, main.getConfig().getInt("inventories.permission-menu.size"), ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("inventories.permission-menu.title")));
        inventory.setItem(getSlot("pick"), getItemStack(player,"pick"));
        inventory.setItem(getSlot("open"), getItemStack(player,"open"));
        inventory.setItem(getSlot("put"), getItemStack(player,"put"));
        player.openInventory(inventory);
    }


    private static ItemStack getItemStack(Player player, String name){
        String path = "inventories.permission-menu.items." + name;
        String status = main.getConfig().getString("inventories.permission-menu.status-prefix.deactive");
        ItemStack itemStack = new ItemStack(Material.getMaterial(main.getConfig().getString(path + ".material")));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString(path + ".title")));
        List<String> lore = new ArrayList<>();
        if (management.checkPermission(player, convert(name))){
            status = main.getConfig().getString("inventories.permission-menu.status-prefix.active");
        }
        itemStack.addUnsafeEnchantment(Enchantment.MENDING, 1);
        for (String config_lore : main.getConfig().getStringList(path + ".lore")){
            lore.add(ChatColor.translateAlternateColorCodes('&', config_lore.replaceAll("%status%", status)));
        }
        meta.setLore(lore);
        if (main.getConfig().getInt(path + ".custom-model-data") != 0){
            meta.setCustomModelData(main.getConfig().getInt(path + ".custom-model-data"));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private static Permissions convert(String value){
        if (value.equalsIgnoreCase("pick")){
            return Permissions.PICK_ITEM;
        } else if (value.equalsIgnoreCase("open")) {
            return Permissions.OPEN;
        } else if (value.equalsIgnoreCase("put")) {
            return Permissions.PUT_ITEM;
        }
        return Permissions.OPEN;
    }

    public static int getSlot(String value){
        return main.getConfig().getInt("inventories.permission-menu.items." + value + ".slot");
    }

}
