package dev.meyba.justAdvent.listeners;

import dev.meyba.justAdvent.JustAdvent;
import dev.meyba.justAdvent.managers.ConfigManager;
import dev.meyba.justAdvent.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDate;
import java.time.Month;

public class AdventListener implements Listener {
    private final JustAdvent plugin;
    private final ConfigManager configManager;
    private final PlayerDataManager playerDataManager;

    public AdventListener(JustAdvent plugin, ConfigManager configManager, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.playerDataManager = playerDataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        LocalDate now = LocalDate.now();
        if (now.getMonth() == Month.DECEMBER) {
            int currentDay = now.getDayOfMonth();
            int currentYear = now.getYear();

            int unclaimedCount = 0;
            for (int day = 1; day <= Math.min(currentDay, 24); day++) {
                if (!playerDataManager.hasClaimed(player.getUniqueId(), day, currentYear)) {
                    unclaimedCount++;
                }
            }

            if (unclaimedCount > 0) {
                String message = configManager.getMessage("notification.unclaimed-rewards", "{count}", String.valueOf(unclaimedCount));
                player.sendMessage(message);
            }
        }
    }
}