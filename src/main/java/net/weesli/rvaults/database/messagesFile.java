package net.weesli.rvaults.database;

import com.google.common.base.Charsets;
import net.weesli.rvaults.RVaults;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class messagesFile {


    private static File configFile;
    private static FileConfiguration newConfig;

    public static  void setupFile() {
        if(!new File(RVaults.getInstance().getDataFolder(),"messages.yml").exists()){
            RVaults.getInstance().saveResource("messages.yml", false);
        }
        configFile = new File(RVaults.getInstance().getDataFolder(),"messages.yml");
    }

    public static FileConfiguration getFile() {
        if (newConfig == null) {
            reloadFile();
        }

        return newConfig;
    }

    public static void saveFile() {
        try {
            getFile().save(configFile);
        } catch (IOException var2) {
            IOException ex = var2;
        }
    }

    public static void reloadFile() {
        newConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defConfigStream = RVaults.getInstance().getResource("messages.yml");
        if (defConfigStream != null) {
            newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        }
    }


}
