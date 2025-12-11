package dev.meyba.justAdvent;

import dev.meyba.justAdvent.commands.AdventCommands;
import dev.meyba.justAdvent.guis.CalendarGUI;
import dev.meyba.justAdvent.listeners.AdventListener;
import dev.meyba.justAdvent.managers.ConfigManager;
import dev.meyba.justAdvent.managers.PlayerDataManager;
import dev.meyba.justAdvent.utils.VersionChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class JustAdvent extends JavaPlugin {

    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private CalendarGUI calendarGUI;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize managers
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);
        calendarGUI = new CalendarGUI(this, configManager, playerDataManager);

        // Register commands
        AdventCommands adventCommands = new AdventCommands(this, configManager, playerDataManager, calendarGUI);
        getCommand("advent").setExecutor(adventCommands);
        getCommand("advent").setTabCompleter(adventCommands);

        // Register listeners
        getServer().getPluginManager().registerEvents(calendarGUI, this);
        getServer().getPluginManager().registerEvents(new AdventListener(this, configManager, playerDataManager), this);

        // Check for updates
        new VersionChecker(this, "RokyYTR2", "JustAdvent").checkForUpdates();

        // Info message
        getLogger().info("JustAdvent has been enabled!");
        getLogger().info("Created by: Meyba._. & Jezevcik20");
    }

    @Override
    public void onDisable() {
        // Save data on shutdown
        if (playerDataManager != null) {
            playerDataManager.saveData();
        }

        getLogger().info("JustAdvent has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public CalendarGUI getCalendarGUI() {
        return calendarGUI;
    }
}
