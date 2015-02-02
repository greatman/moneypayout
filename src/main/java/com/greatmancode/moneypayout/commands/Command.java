package com.greatmancode.moneypayout.commands;

import org.bukkit.command.CommandSender;

public interface Command {

    public void execute(CommandSender sender, String[] args);

    public int minArgs();

    public int maxArgs();

    public String help();

    public boolean playerOnly();

    public String getPermissionNode();

}
