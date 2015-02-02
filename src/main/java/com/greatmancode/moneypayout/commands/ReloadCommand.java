package com.greatmancode.moneypayout.commands;

import com.greatmancode.moneypayout.MoneyPayoutPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends AbstractCommand {


    public ReloadCommand(MoneyPayoutPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        getPlugin().reloadConfig();
        sender.sendMessage(PREFIX + "Reloaded!");
    }

    @Override
    public String help() {
        return "/moneypayout reload - Reloads the config";
    }

    @Override
    public int maxArgs() {
        return 0;
    }

    @Override
    public int minArgs() {
        return 0;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermissionNode() {
        return "moneypayout.reload";
    }
}
