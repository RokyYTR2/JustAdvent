package dev.meyba.justAdvent.managers;

import dev.meyba.justAdvent.JustAdvent;
import org.bukkit.command.CommandSender;

public class ChatManager {
    private final JustAdvent plugin;

    public ChatManager(JustAdvent plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(CommandSender sender, String messageKey) {
        String message = plugin.getConfigManager().getMessage(messageKey);
        sender.sendMessage(message);
    }

    public void sendMessage(CommandSender sender, String messageKey, String placeholder, String value) {
        String message = plugin.getConfigManager().getMessage(messageKey, placeholder, value);
        sender.sendMessage(message);
    }

    public String getPrefix() {
        return plugin.getConfigManager().getMessage("prefix");
    }
}
