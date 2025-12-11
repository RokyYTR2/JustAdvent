package dev.meyba.justAdvent.managers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ConfigManager {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration messages;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    public void loadConfigs() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defMessagesStream = plugin.getResource("messages.yml");
        if (defMessagesStream != null) {
            YamlConfiguration defMessages = YamlConfiguration.loadConfiguration(new InputStreamReader(defMessagesStream));
            messages.setDefaults(defMessages);
        }
    }

    public void reloadConfigs() {
        loadConfigs();
    }

    public String getMessage(String path) {
        String message = messages.getString(path);
        if (message == null) {
            return ChatColor.RED + "Missing message: " + path;
        }

        String prefix = messages.getString("prefix", "");
        message = message.replace("{prefix}", prefix);

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String path, String placeholder, String value) {
        String message = getMessage(path);
        return message.replace(placeholder, value);
    }

    public String getGuiTitle() {
        String title = config.getString("gui-title", "&c&lAdvent Calendar");
        return ChatColor.translateAlternateColorCodes('&', title);
    }

    public int getGuiSize() {
        int size = config.getInt("gui-size", 54);
        if (size % 9 != 0 || size < 9 || size > 54) {
            size = 54;
        }
        return size;
    }

    public String getRewardDisplayName(int day) {
        String name = config.getString("rewards." + day + ".display-name", "&cDecember " + day);
        return ChatColor.translateAlternateColorCodes('&', name);
    }

    public String getRewardMaterial(int day) {
        return config.getString("rewards." + day + ".material", "CHEST");
    }

    public List<String> getRewardLore(int day) {
        List<String> lore = config.getStringList("rewards." + day + ".lore");
        lore.replaceAll(line -> ChatColor.translateAlternateColorCodes('&', line));
        return lore;
    }

    public List<String> getRewardCommands(int day) {
        return config.getStringList("rewards." + day + ".commands");
    }

    public String getClaimedItemMaterial() {
        return config.getString("claimed-item.material", "LIME_STAINED_GLASS_PANE");
    }

    public String getClaimedItemDisplayName(int day) {
        String name = config.getString("claimed-item.display-name", "&a&lClaimed Day {day}");
        name = name.replace("{day}", String.valueOf(day));
        return ChatColor.translateAlternateColorCodes('&', name);
    }

    public List<String> getClaimedItemLore() {
        List<String> lore = config.getStringList("claimed-item.lore");
        lore.replaceAll(line -> ChatColor.translateAlternateColorCodes('&', line));
        return lore;
    }

    public String getLockedItemMaterial() {
        return config.getString("locked-item.material", "RED_STAINED_GLASS_PANE");
    }

    public String getLockedItemDisplayName(int day) {
        String name = config.getString("locked-item.display-name", "&c&lLocked - December {day}");
        name = name.replace("{day}", String.valueOf(day));
        return ChatColor.translateAlternateColorCodes('&', name);
    }

    public List<String> getLockedItemLore(int day) {
        List<String> lore = config.getStringList("locked-item.lore");
        lore.replaceAll(line -> ChatColor.translateAlternateColorCodes('&', line.replace("{day}", String.valueOf(day))));
        return lore;
    }
}