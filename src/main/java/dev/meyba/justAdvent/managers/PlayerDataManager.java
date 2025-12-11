package dev.meyba.justAdvent.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDataManager {
    private final JavaPlugin plugin;
    private final File dataFile;
    private FileConfiguration data;

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Cannot create playerdata.yml!");
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Cannot save playerdata.yml!");
            e.printStackTrace();
        }
    }

    public void reloadData() {
        loadData();
    }

    public boolean hasClaimed(UUID playerUUID, int day, int year) {
        String path = playerUUID.toString() + "." + year + "." + day;
        return data.getBoolean(path, false);
    }

    public void setClaimed(UUID playerUUID, int day, int year) {
        String path = playerUUID.toString() + "." + year + "." + day;
        data.set(path, true);
        saveData();
    }

    public List<Integer> getClaimedDays(UUID playerUUID, int year) {
        List<Integer> claimedDays = new ArrayList<>();
        String basePath = playerUUID.toString() + "." + year;

        if (data.getConfigurationSection(basePath) != null) {
            for (String key : data.getConfigurationSection(basePath).getKeys(false)) {
                try {
                    int day = Integer.parseInt(key);
                    if (data.getBoolean(basePath + "." + key)) {
                        claimedDays.add(day);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        return claimedDays;
    }
}