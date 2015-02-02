package com.greatmancode.moneypayout;

import org.bukkit.entity.Player;

import java.util.*;

public class MoneyGiver implements Runnable {

    private final MoneyPayoutPlugin plugin;
    private final Map<UUID, Double> todayGiven = new HashMap<UUID, Double>();

    public MoneyGiver(MoneyPayoutPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (final Map.Entry<UUID, Long> playerMap : plugin.getAfkTimerList().entrySet()) {
            Player player = plugin.getServer().getPlayer(playerMap.getKey());
            if (player == null) {
                plugin.removePlayerFromAfkTimer(playerMap.getKey());
                continue;
            }

            Set<String> groups = plugin.getConfig().getConfigurationSection("Group").getKeys(false);
            String group = "default";
            for (String groupEntry: groups) {
                if (player.hasPermission("moneypayout.group." +groupEntry)) {
                    group = groupEntry;
                    break;
                }
            }

            int timeout = plugin.getConfig().getInt("Group." + group + ".AFKTimeout");
            double money = plugin.getConfig().getDouble("Group." + group + ".MoneyPerMinute");
            double maxAmount = plugin.getConfig().getDouble("Group." + group + ".MaxMoneyEarnPerDay");
            if ((timeout != -1 || playerMap.getValue() - System.currentTimeMillis() > timeout * 1000 * 60)) {
                if (!todayGiven.containsKey(player.getUniqueId())) {
                    plugin.getBackend().savePlayer(player, money);
                    todayGiven.put(player.getUniqueId(), money);
                } else if (!(todayGiven.get(player.getUniqueId()) + money > maxAmount)) {
                    plugin.getBackend().savePlayer(player, plugin.getBackend().retrievePlayer(player) + money);
                    todayGiven.put(player.getUniqueId(), todayGiven.get(player.getUniqueId()) + money);
                }
            }
        }
    }

    public void resetToday() {
        todayGiven.clear();
    }
}
