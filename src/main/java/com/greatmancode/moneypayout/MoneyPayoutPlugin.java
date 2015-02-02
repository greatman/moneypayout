package com.greatmancode.moneypayout;

import com.greatmancode.moneypayout.backend.Backend;
import com.greatmancode.moneypayout.backend.MySQLBackend;
import com.greatmancode.moneypayout.commands.*;
import com.greatmancode.moneypayout.listeners.BlockListener;
import com.greatmancode.moneypayout.listeners.PlayerListener;
import jdk.nashorn.internal.objects.annotations.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoneyPayoutPlugin extends JavaPlugin {

    public static final int ONE_DAY_IN_TICKS = 20 * 60 * 60 * 24;
    public static final int ONE_MINUTE_IN_TICKS = 20 * 60;

    private Map<UUID, Long> afkTimer = new HashMap<UUID, Long>();
    private Economy econ;
    private Permission perms;
    private Backend backend;
    private MoneyGiver moneyGiver;
    private Map<String, AbstractCommand> commandList = new HashMap<String, AbstractCommand>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        //getConfig().options().copyDefaults(true);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        if (!setupEconomy() || !setupPermissions()) {
            getLogger().severe("Missing either a economy plugin or a permission plugin! Disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String databaseType = getConfig().getString("databaseType");

        if (databaseType.equalsIgnoreCase("mysql")) {
            try {
                backend = new MySQLBackend(this);
            } catch (SQLException e) {
                e.printStackTrace();
                getServer().getPluginManager().disablePlugin(this);
            }
        } else {
            getLogger().severe("Unknown backend type: " + databaseType + "! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
        }
        commandList.put("add", new AddCommand(this));
        commandList.put("cashout", new CashoutCommand(this));
        commandList.put("reload", new ReloadCommand(this));
        commandList.put("remove", new RemoveCommand(this));
        commandList.put("stats", new StatsCommand(this));
        commandList.put("", new HelpCommand(this));

        moneyGiver = new MoneyGiver(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, moneyGiver, ONE_MINUTE_IN_TICKS, ONE_MINUTE_IN_TICKS);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                moneyGiver.resetToday();
            }
        },ONE_DAY_IN_TICKS, ONE_DAY_IN_TICKS);

        File file = new File(getDataFolder().getAbsolutePath() + File.separator + "money.db");
        if (file.exists()) {
            getLogger().info("Saving old database data");
            ObjectInputStream stream = null;
            Map<String, Double> entries = new HashMap<String, Double>();
            try {
                stream = new ObjectInputStream(new FileInputStream(file));
                stream.readInt();

                int size = stream.readInt();
                getLogger().info("Saving " + size + " accounts");
                for (int i = 0; i < size; i++) {
                    String player = stream.readUTF();
                    double value = stream.readDouble();
                    entries.put(player, value);
                    if (i % 10 == 0) {
                        getLogger().info(i + " of " + size + " saved!");
                    }
                }
                backend.importPlayers(entries);
            } catch (IOException e) {
                e.printStackTrace();
                backend.importPlayers(entries);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDisable() {
        econ = null;
        perms = null;
        backend.disable();
        backend = null;
        moneyGiver = null;
        getServer().getScheduler().cancelTasks(this);
        afkTimer.clear();
        commandList.clear();
    }

    public void addPlayerToAFKTimer(UUID uuid, Long time) {
        afkTimer.put(uuid, time);
    }

    public void removePlayerFromAfkTimer(UUID uuid) {
        afkTimer.remove(uuid);
    }

    public Economy getEconomy() {
        return econ;
    }

    public Permission getPermissions() {
        return perms;
    }

    public double cashOut(OfflinePlayer p) {
        double money = getBackend().retrievePlayer(p);
        getEconomy().depositPlayer(p, null, money);
        getBackend().removePlayer(p);
        return money;
    }

    public Backend getBackend() {
        return backend;
    }

    public Map<UUID,Long> getAfkTimerList() {
        return Collections.unmodifiableMap(afkTimer);
    }

    public Map<String, AbstractCommand> getCommandList() {
        return Collections.unmodifiableMap(commandList);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String subCommandValue = "";
        String[] newArgs;
        if (args.length <= 1) {
            newArgs = new String[0];
            if (args.length != 0) {
                subCommandValue = args[0];
            }
        } else {
            newArgs = new String[args.length - 1];
            subCommandValue = args[0];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        }
        commandList.get(subCommandValue).execute(sender, newArgs);
        return true;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}
