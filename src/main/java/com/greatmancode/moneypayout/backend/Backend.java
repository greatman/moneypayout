package com.greatmancode.moneypayout.backend;

import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.UUID;

public interface Backend {

    public void savePlayer(OfflinePlayer p, double value);

    public double retrievePlayer(OfflinePlayer p);

    public void removePlayer(OfflinePlayer p);

    public void disable();

    public void importPlayers(Map<String, Double> players);
}
