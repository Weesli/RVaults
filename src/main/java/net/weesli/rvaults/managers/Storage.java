package net.weesli.rvaults.managers;

import net.weesli.rvaults.RVaults;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Storage {

    private UUID owner;
    private List<UUID> members;
    private List<Permissions> permissions;
    private int size;
    private Inventory inventory;

    public Storage(UUID owner, List<UUID> members, List<Permissions> permissions, int size){
        this.owner = owner;
        this.members = members;
        this.permissions = permissions;
        this.size = size;
        if (this.inventory == null){
            this.inventory = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&' , RVaults.getInstance().getConfig().getString("options.menu-title").replaceAll("%player%", Bukkit.getOfflinePlayer(owner).getName())));

        }
    }

    public UUID getOwner() {
        return owner;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public List<Permissions> getPermissions() {
        return permissions;
    }

    public int getSize() {
        return size;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setSize(int size) {
        int last_size = this.size;
        this.size = size;
        @NotNull List<HumanEntity> viewers = this.inventory.getViewers();
        // if new size smaller than last size, all items rotate to first slots but new size bigger than last size, all items stay in it position
        Inventory new_inventory = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&' , RVaults.getInstance().getConfig().getString("options.menu-title").replaceAll("%player%", Bukkit.getOfflinePlayer(owner).getName())));
        if (last_size > size){
            for (ItemStack itemStack : inventory){
                if (itemStack == null){continue;}
                new_inventory.addItem(itemStack);
            }
        }else {
            int a = 0;
            for (ItemStack itemStack : inventory.getContents()){
                if (itemStack == null){a++; continue;}
                new_inventory.setItem(a, itemStack);
            }
        }
        this.inventory = new_inventory;
        Iterator<HumanEntity> entities = viewers.iterator();
        if (!viewers.isEmpty()) {
            HumanEntity entity = entities.next();
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.closeInventory();
                player.openInventory(this.inventory);
            }
        }
    }

    public void addMember(UUID member){
        members.add(member);
    }
    public void removeMember(UUID member){
        members.remove(member);
    }

    public void addPermission(Permissions permissions){
        getPermissions().add(permissions);
    }

    public void removePermission(Permissions permissions){
        getPermissions().remove(permissions);
    }

    public void setInventory(Inventory inventory){
        this.inventory = inventory;
    }
    public Inventory getInventory(){
        return inventory;
    }

}
