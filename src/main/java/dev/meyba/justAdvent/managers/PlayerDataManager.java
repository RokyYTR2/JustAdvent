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

    /**
     * Checks if player has already claimed reward for given day
     */
    public boolean hasClaimed(UUID playerUUID, int day, int year) {
        String path = playerUUID.toString() + "." + year + "." + day;
        return data.getBoolean(path, false);
    }

    /**
     * Marks reward as claimed for given player
     */
    public void setClaimed(UUID playerUUID, int day, int year) {
        String path = playerUUID.toString() + "." + year + "." + day;
        data.set(path, true);
        saveData();
    }

    /**
     * Gets all days that player claimed in given year
     */
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
                } catch (NumberFormatException e) {
                    // Ignore invalid keys
                }
            }
        }

        return claimedDays;
    }

    /**
     * Clears all data for given year (useful for reset)
     */
    public void clearYear(int year) {
        for (String uuid : data.getKeys(false)) {
            String path = uuid + "." + year;
            data.set(path, null);
        }
        saveData();
    }

    /**
     * Clears all player data
     */
    public void clearPlayer(UUID playerUUID) {
        data.set(playerUUID.toString(), null);
        saveData();
    }

    /**
     * Gets statistics - how many players claimed given reward
     */
    public int getClaimCount(int day, int year) {
        int count = 0;
        for (String uuid : data.getKeys(false)) {
            String path = uuid + "." + year + "." + day;
            if (data.getBoolean(path, false)) {
                count++;
            }
        }
        return count;
    }
}
