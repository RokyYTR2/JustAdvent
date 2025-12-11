package dev.meyba.justAdvent.guis;

import dev.meyba.justAdvent.JustAdvent;
import dev.meyba.justAdvent.managers.ConfigManager;
import dev.meyba.justAdvent.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarGUI implements Listener {
    private static final String GUI_NAME_PREFIX = ChatColor.translateAlternateColorCodes('&', "&lᴀᴅᴠᴇɴᴛ ᴄᴀʟᴇɴᴅᴀʀ");
    private final JustAdvent plugin;
    private final ConfigManager configManager;
    private final PlayerDataManager playerDataManager;

    public CalendarGUI(JustAdvent plugin, ConfigManager configManager, PlayerDataManager playerDataManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.playerDataManager = playerDataManager;
    }

    public void openCalendar(Player player) {
        int size = configManager.getGuiSize();
        Inventory inventory = Bukkit.createInventory(null, size, GUI_NAME_PREFIX);

        setupInventory(inventory, player);

        player.openInventory(inventory);
        player.sendMessage(configManager.getMessage("gui.opened"));
    }

    private void setupInventory(Inventory inventory, Player player) {
        inventory.clear();

        ItemStack grayGlassPane = createItem(Material.GRAY_STAINED_GLASS_PANE, "§r", Collections.emptyList());

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, grayGlassPane);
        }
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, grayGlassPane);
        }

        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, grayGlassPane);
            inventory.setItem(i + 8, grayGlassPane);
        }

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentDay = now.getDayOfMonth();
        Month currentMonth = now.getMonth();

        for (int day = 1; day <= 24; day++) {
            ItemStack item;

            boolean claimed = playerDataManager.hasClaimed(player.getUniqueId(), day, currentYear);

            boolean isDecember = (currentMonth == Month.DECEMBER);
            boolean dayAvailable = isDecember && currentDay >= day;

            if (claimed) {
                item = createClaimedItem(day);
            } else if (!dayAvailable) {
                item = createLockedItem(day);
            } else {
                item = createRewardItem(day);
            }

            int slot = getSlotForDay(day);
            inventory.setItem(slot, item);
        }
    }

    private int getSlotForDay(int day) {
        int row = (day - 1) / 6;
        int col = (day - 1) % 6;
        return 10 + (row * 9) + col;
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            meta.setLore(lore.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createClaimedItem(int day) {
        Material material = Material.getMaterial(configManager.getClaimedItemMaterial());
        if (material == null) material = Material.LIME_STAINED_GLASS_PANE;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(configManager.getClaimedItemDisplayName(day));
            meta.setLore(configManager.getClaimedItemLore());
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createLockedItem(int day) {
        Material material = Material.getMaterial(configManager.getLockedItemMaterial());
        if (material == null) material = Material.RED_STAINED_GLASS_PANE;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(configManager.getLockedItemDisplayName(day));
            meta.setLore(configManager.getLockedItemLore(day));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createRewardItem(int day) {
        Material material = Material.getMaterial(configManager.getRewardMaterial(day));
        if (material == null) material = Material.CHEST;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(configManager.getRewardDisplayName(day));
            meta.setLore(configManager.getRewardLore(day));
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith(GUI_NAME_PREFIX)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        int day = getDayFromSlot(event.getSlot());
        if (day == -1) {
            return;
        }

        handleDayClick(player, day);
    }

    private int getDayFromSlot(int slot) {
        for (int day = 1; day <= 24; day++) {
            if (getSlotForDay(day) == slot) {
                return day;
            }
        }
        return -1;
    }

    private void handleDayClick(Player player, int day) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentDay = now.getDayOfMonth();
        Month currentMonth = now.getMonth();

        if (playerDataManager.hasClaimed(player.getUniqueId(), day, currentYear)) {
            player.sendMessage(configManager.getMessage("reward.already-claimed", "{day}", String.valueOf(day)));
            return;
        }

        boolean isDecember = (currentMonth == Month.DECEMBER);
        if (!isDecember) {
            player.sendMessage(configManager.getMessage("reward.not-december"));
            player.closeInventory();
            return;
        }

        if (currentDay < day) {
            player.sendMessage(configManager.getMessage("reward.locked", "{day}", String.valueOf(day)));
            return;
        }

        giveReward(player, day, currentYear);
    }

    private void giveReward(Player player, int day, int year) {
        List<String> commands = configManager.getRewardCommands(day);

        if (commands.isEmpty()) {
            player.sendMessage(configManager.getMessage("error.invalid-day"));
            return;
        }

        for (String command : commands) {
            String finalCommand = command.replace("{player}", player.getName());
            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            } catch (Exception e) {
                player.sendMessage(configManager.getMessage("error.command-failed"));
                plugin.getLogger().warning("Error executing command: " + finalCommand);
                e.printStackTrace();
                return;
            }
        }

        playerDataManager.setClaimed(player.getUniqueId(), day, year);

        player.sendMessage(configManager.getMessage("reward.claimed", "{day}", String.valueOf(day)));

        player.closeInventory();
        Bukkit.getScheduler().runTaskLater(plugin, () -> openCalendar(player), 2L);
    }
}