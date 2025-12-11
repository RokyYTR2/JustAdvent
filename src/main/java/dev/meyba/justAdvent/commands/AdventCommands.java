package dev.meyba.justAdvent.commands;

import dev.meyba.justAdvent.JustAdvent;
import dev.meyba.justAdvent.guis.CalendarGUI;
import dev.meyba.justAdvent.managers.ConfigManager;
import dev.meyba.justAdvent.managers.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AdventCommands implements CommandExecutor, TabCompleter {
    private final JustAdvent plugin;
    private final ConfigManager configManager;
    private final PlayerDataManager playerDataManager;
    private final CalendarGUI calendarGUI;

    public AdventCommands(JustAdvent plugin, ConfigManager configManager, PlayerDataManager playerDataManager, CalendarGUI calendarGUI) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.playerDataManager = playerDataManager;
        this.calendarGUI = calendarGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(configManager.getMessage("command.only-player"));
                return true;
            }

            calendarGUI.openCalendar(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                if (!sender.hasPermission("justadvent.reload")) {
                    sender.sendMessage(configManager.getMessage("command.no-permission"));
                    return true;
                }

                configManager.reloadConfigs();
                playerDataManager.reloadData();
                sender.sendMessage(configManager.getMessage("command.reload"));
                return true;

            case "open":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(configManager.getMessage("command.only-player"));
                    return true;
                }

                calendarGUI.openCalendar(player);
                return true;

            case "help":
                sendHelp(sender);
                return true;

            default:
                sender.sendMessage(configManager.getMessage("command.unknown"));
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        String prefix = configManager.getMessage("prefix");
        sender.sendMessage(prefix + "§7ʜᴇʟᴘ ᴍᴇɴᴜ:");
        sender.sendMessage(prefix + "§7/advent - ᴏᴘᴇɴꜱ ᴛʜᴇ ᴀᴅᴠᴇɴᴛ ᴄᴀʟᴇɴᴅᴀʀ.");
        sender.sendMessage(prefix + "§7/advent open - ᴏᴘᴇɴꜱ ᴛʜᴇ ᴀᴅᴠᴇɴᴛ ᴄᴀʟᴇɴᴅᴀʀ.");
        sender.sendMessage(prefix + "§7/advent help - ꜱʜᴏᴡꜱ ᴛʜɪꜱ ʜᴇʟᴘ ᴍᴇɴᴜ.");
        if (sender.hasPermission("justadvent.reload")) {
            sender.sendMessage(prefix + "§7/advent reload - ʀᴇʟᴏᴀᴅꜱ ᴛʜᴇ ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("open");
            completions.add("help");
            if (sender.hasPermission("justadvent.reload")) {
                completions.add("reload");
            }

            String input = args[0].toLowerCase();
            completions.removeIf(s -> !s.toLowerCase().startsWith(input));
        }

        return completions;
    }
}