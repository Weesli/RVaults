package net.weesli.rvaults.Commands;

import net.weesli.rvaults.UI.BaseUI;
import net.weesli.rvaults.database.messagesFile;
import net.weesli.rvaults.managers.Permissions;
import net.weesli.rvaults.managers.Storage;
import net.weesli.rvaults.managers.StorageManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StorageCommands implements CommandExecutor, TabCompleter {

    StorageManagement management = new StorageManagement();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player){
            Player player = ((Player) commandSender).getPlayer();
            if (args.length == 0){
                management.openStorage(player);
            } else if (args[0].equalsIgnoreCase("permission")) {
                if (!management.checkStorage(player)){
                    management.createStorage(player, management.getStarterSize());
                }
                BaseUI.openPermissionMenu(player);
            } else if (args[0].equalsIgnoreCase("addplayer")) {
                if (args.length > 1){
                    if (!management.checkStorage(player)){
                        management.createStorage(player, management.getStarterSize());
                    }
                    boolean isActive = false;
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {if (offlinePlayer.getName().equals(args[1])){isActive = true;break;}}
                    if (!isActive) {player.sendMessage(management.getMessage("PLAYER-NOT-FOUND"));return false;}
                    if (args[1].equals(player.getName())){player.sendMessage(management.getMessage("NOT-ADD-YOU-SELF"));return false;}
                    Storage storage = management.getPlayerStorage(player);
                    if (storage == null){return false;}
                    if (storage.getMembers().contains(Bukkit.getOfflinePlayer(args[1]).getUniqueId())){
                        player.sendMessage(management.getMessage("PLAYER-ALREADY-ADDED-STORAGE"));
                        return false;
                    }
                    storage.addMember(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                    player.sendMessage(management.getMessage("ADDED-PLAYER").replaceAll("%player%", Bukkit.getOfflinePlayer(args[1]).getName()));
                }else {
                    player.sendMessage(management.getMessage("NOT-PLAYER-ARGUMENT"));
                }
            } else if (args[0].equals("removeplayer")) {
                if (args.length > 1){
                    if (!management.checkStorage(player)){
                        management.createStorage(player,management.getStarterSize());
                    }
                    boolean isActive = false;
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {if (offlinePlayer.getName().equals(args[1])){isActive = true;break;}}
                    if (!isActive) {player.sendMessage(management.getMessage("PLAYER-NOT-FOUND"));return false;}
                    if (args[1].equals(player.getName())){player.sendMessage(management.getMessage("NOT-REMOVE-YOU-SELF"));return false;}
                    Storage storage = management.getPlayerStorage(player);
                    if (storage == null){return false;}
                    if (!storage.getMembers().contains(Bukkit.getOfflinePlayer(args[1]).getUniqueId())){
                        player.sendMessage(management.getMessage("PLAYER-ALREADY-NOT-CONTAIN-IN-STORAGE"));
                        return false;
                    }
                    storage.removeMember(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                    player.sendMessage(management.getMessage("REMOVE-PLAYER").replaceAll("%player%", Bukkit.getOfflinePlayer(args[1]).getName()));
                }else {
                    player.sendMessage(management.getMessage("NOT-PLAYER-ARGUMENT"));
                }
            } else if (args[0].equalsIgnoreCase("open")) {
                if (args.length > 1){
                    boolean isActive = false;
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {if (offlinePlayer.getName().equals(args[1])){isActive = true;break;}}
                    if (!isActive) {player.sendMessage(management.getMessage("PLAYER-NOT-FOUND"));return false;}
                    UUID target = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
                    if (target.equals(player.getUniqueId())){
                        player.openInventory(management.getStorage().get(target).getInventory());
                        return false;
                    }
                    if (!management.CheckMemberinPlayer(target,player.getUniqueId())){
                        player.sendMessage(management.getMessage("YOU-NOT-CONTAIN-STORAGE"));
                        return false;
                    }
                    Storage storage = management.getStorage().get(target);
                    if (storage == null){
                        return false;
                    }
                    if (storage.getPermissions().contains(Permissions.OPEN)){
                        player.sendMessage(management.getMessage("OPEN-PERMISSION"));
                        return false;
                    }
                    int max_size = management.getStarterSize() / 9;
                    for (PermissionAttachmentInfo ignored : Bukkit.getOfflinePlayer(args[1]).getPlayer().getEffectivePermissions()){
                        String[] x = ignored.getPermission().split("[.]");
                        String permission =x[0] + "." + x[1];
                        if (permission.equalsIgnoreCase("rvaults.size")){
                            if (max_size < Integer.parseInt(x[2])){
                                max_size = Integer.parseInt(x[2]);
                            }
                        }
                    }
                    if (max_size != management.getStarterSize()){
                        management.getPlayerStorage(Bukkit.getOfflinePlayer(args[1]).getPlayer()).setSize(max_size * 9);
                    }
                    player.openInventory(storage.getInventory());
                }else {
                    player.sendMessage(management.getMessage("NOT-PLAYER-ARGUMENT"));
                }
            } else if (args[0].equalsIgnoreCase("admin")){
                if (!commandSender.hasPermission("rvaults.admin")){
                    commandSender.sendMessage(management.getMessage("NO-PERMISSON"));
                    return false;
                }
                if (args.length == 1){
                    commandSender.sendMessage(ChatColor.YELLOW + "Author by Weesli");
                    for (String x : messagesFile.getFile().getStringList("admin-help")){
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', x));
                    }
                } else if (args[1].equalsIgnoreCase("delete")) {
                    if (args.length > 2){
                        boolean isActive = false;
                        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {if (offlinePlayer.getName().equals(args[2])){isActive = true;break;}}
                        if (!isActive) {commandSender.sendMessage(management.getMessage("PLAYER-NOT-FOUND"));return false;}
                        Storage storage = management.getStorage().get(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
                        if (storage == null){
                            commandSender.sendMessage(management.getMessage("PLAYER-NOT-HAVE-STORAGE"));
                            return false;
                        }
                        management.getStorage().remove(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
                        management.DeleteData(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
                        commandSender.sendMessage(management.getMessage("DELETED-PLAYER-STORAGE"));
                    }else {
                        commandSender.sendMessage(management.getMessage("NOT-PLAYER-ARGUMENT"));
                    }
                } else if (args[1].equalsIgnoreCase("open")) {
                    if (commandSender instanceof Player){
                        if (args.length > 2){
                            boolean isActive = false;
                            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {if (offlinePlayer.getName().equals(args[2])){isActive = true;break;}}
                            if (!isActive) {commandSender.sendMessage(management.getMessage("PLAYER-NOT-FOUND"));return false;}
                            Storage storage = management.getStorage().get(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
                            if (storage == null){
                                commandSender.sendMessage(management.getMessage("PLAYER-NOT-HAVE-STORAGE"));
                                return false;
                            }
                            int max_size = management.getStarterSize() / 9;
                            for (PermissionAttachmentInfo ignored : Bukkit.getOfflinePlayer(args[2]).getPlayer().getEffectivePermissions()){
                                String[] x = ignored.getPermission().split("[.]");
                                String permission =x[0] + "." + x[1];
                                if (permission.equalsIgnoreCase("rvaults.size")){
                                    if (max_size < Integer.parseInt(x[2])){
                                        max_size = Integer.parseInt(x[2]);
                                    }
                                }
                            }
                            if (max_size != management.getStarterSize()){
                                management.getPlayerStorage(Bukkit.getOfflinePlayer(args[2]).getPlayer()).setSize(max_size * 9);
                            }
                            player.openInventory(storage.getInventory());
                        }else {
                            commandSender.sendMessage(management.getMessage("NOT-PLAYER-ARGUMENT"));
                        }
                    }
                }
            }

        }else{
            commandSender.sendMessage(ChatColor.RED
             + "This command only works for the player");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>();
        arguments.add("addplayer");
        arguments.add("removeplayer");
        arguments.add("open");
        arguments.add("permission");
        if (args[0].equalsIgnoreCase("open")){
            List<String> players = new ArrayList<>();
            for (OfflinePlayer target : Bukkit.getOfflinePlayers()){
                Player player = (Player) commandSender;
                if (management.getStorage().get(target.getUniqueId()) == null){continue;}
                if (management.getStorage().get(target.getUniqueId()).getMembers().contains(player.getUniqueId())){
                    players.add(target.getName());
                }
            }
            return players;
        }
        if (args[0].equalsIgnoreCase("removeplayer")){
            Player player = (Player) commandSender;
            if (management.getStorage().get(player.getUniqueId()) == null){return Arrays.asList("");}
            List<String> players = new ArrayList<>();
            for (UUID uuid : management.getStorage().get(player.getUniqueId()).getMembers()){
                players.add(Bukkit.getOfflinePlayer(uuid).getName());
            }
            return players;
        }
        if (args[0].equalsIgnoreCase("addplayer")){
            List<String> players = new ArrayList<>();
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()){
                players.add(player.getName());
            }
            return players;
        }
        return arguments;
    }
}
