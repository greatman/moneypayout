package com.greatmancode.moneypayout.commands;

import com.greatmancode.moneypayout.MoneyPayoutPlugin;
import com.greatmancode.moneypayout.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class AddCommand extends AbstractCommand {

    public AddCommand(MoneyPayoutPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (Tools.isValidDouble(args[1])) {
            double amount = Double.parseDouble(args[1]);
            OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
            if (p != null) {
                getPlugin().getBackend().savePlayer(p, getPlugin().getBackend().retrievePlayer(p) + amount);
                sender.sendMessage(PREFIX + "Amount added to player " + ChatColor.WHITE + p.getName());
            }
        } else {
            sender.sendMessage(PREFIX + ChatColor.DARK_RED + "Invalid amount!");
        }
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public int maxArgs() {
        return 2;
    }

    @Override
    public String help() {
        return "/moneypayout add <Player> <Amount> - Add money to a moneypayout account";
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermissionNode() {
        return "moneypayout.add";
    }
}
