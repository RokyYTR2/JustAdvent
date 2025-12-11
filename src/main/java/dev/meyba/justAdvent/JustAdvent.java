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
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);
        calendarGUI = new CalendarGUI(this, configManager, playerDataManager);

        AdventCommands adventCommands = new AdventCommands(this, configManager, playerDataManager, calendarGUI);
        getCommand("advent").setExecutor(adventCommands);
        getCommand("advent").setTabCompleter(adventCommands);

        getServer().getPluginManager().registerEvents(calendarGUI, this);
        getServer().getPluginManager().registerEvents(new AdventListener(this, configManager, playerDataManager), this);

        new VersionChecker(this, "RokyYTR2", "JustAdvent").checkForUpdates();

        getLogger().info("JustAdvent has been enabled!");
    }

    @Override
    public void onDisable() {
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