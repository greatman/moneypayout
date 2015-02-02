package com.greatmancode.moneypayout.commands;

import com.greatmancode.moneypayout.MoneyPayoutPlugin;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CashoutCommand extends AbstractCommand {

    public CashoutCommand(MoneyPayoutPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 && !(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.DARK_RED + "You need to be a player to use this command without parameters");
            return;
        }
        String playerName = sender.getName();
        if (args.length == 1 && sender.hasPermission("moneypayout.cashout.other")) {
            playerName = args[0];
        }
        OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
        if (p != null) {
            double value = getPlugin().getBackend().retrievePlayer(p);
            EconomyResponse response = getPlugin().getEconomy().depositPlayer(p, value);
            if (response.transactionSuccess()) {
                getPlugin().getBackend().removePlayer(p);
                sender.sendMessage(PREFIX + "Cashout completed! You received " + getPlugin().getEconomy().format(value));
            } else {
                sender.sendMessage(PREFIX + ChatColor.DARK_RED + "A error occured!");
            }
        }
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
        return "/moneypayout cashout [Player] - Cash out [Player]s money to their bank account.";
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermissionNode() {
        return "moneypayout.cashout.self";
    }
}
