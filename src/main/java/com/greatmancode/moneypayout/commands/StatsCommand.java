package com.greatmancode.moneypayout.commands;

import com.greatmancode.moneypayout.MoneyPayoutPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand extends AbstractCommand {

    public StatsCommand(MoneyPayoutPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.DARK_RED + "You need to be a player to use this command without parameters");
            return;
        }
        String playerName = sender.getName();
        if (args.length == 1 && sender.hasPermission("moneypayout.stats.other")) {
            playerName = args[0];
        }
        OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
        sender.sendMessage(PREFIX + "Player " + playerName + " balance:" + getPlugin().getBackend().retrievePlayer(p));
    }

    @Override
    public int minArgs() {
        return 0;
    }

    @Override
    public int maxArgs() {
        return 1;
    }

    @Override
    public String help() {
        return "/moneypayout stats [Player] - Shows the money earned for [Player]";
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermissionNode() {
        return "moneypayout.stats.self";
    }
}
