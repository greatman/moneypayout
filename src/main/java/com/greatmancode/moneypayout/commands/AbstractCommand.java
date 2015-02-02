package com.greatmancode.moneypayout.commands;

import com.greatmancode.moneypayout.MoneyPayoutPlugin;
import org.bukkit.ChatColor;

public abstract class AbstractCommand implements Command {

    public static final String PREFIX = ChatColor.DARK_GREEN + "[" + ChatColor.WHITE + "MoneyPayout" + ChatColor.DARK_GREEN + "] ";
    private final MoneyPayoutPlugin plugin;

    public AbstractCommand(MoneyPayoutPlugin plugin) {
        this.plugin = plugin;
    }

    public MoneyPayoutPlugin getPlugin() {
        return plugin;
    }
}
