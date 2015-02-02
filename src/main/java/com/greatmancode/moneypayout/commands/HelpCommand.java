package com.greatmancode.moneypayout.commands;

import com.greatmancode.moneypayout.MoneyPayoutPlugin;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class HelpCommand extends AbstractCommand {

    public HelpCommand(MoneyPayoutPlugin plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (Map.Entry<String, AbstractCommand> command : getPlugin().getCommandList().entrySet()) {
            sender.sendMessage(PREFIX + command.getValue().help());
        }
    }

    @Override
    public int minArgs() {
        return 0;
    }

    @Override
    public int maxArgs() {
        return 0;
    }

    @Override
    public String help() {
        return "/moneypayout help - Show thte help";
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermissionNode() {
        return null;
    }
}
