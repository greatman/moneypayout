package com.greatmancode.moneypayout.listeners;

import com.greatmancode.moneypayout.MoneyPayoutPlugin;
import com.greatmancode.moneypayout.commands.AbstractCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public class BlockListener implements Listener{

    private final MoneyPayoutPlugin plugin;

    public BlockListener(MoneyPayoutPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Sign sign;

        if (!event.isCancelled()
                && event.getBlock() != null
                && (event.getBlock().getType() == Material.WALL_SIGN || event
                .getBlock().getType() == Material.SIGN_POST)) {
            sign = (Sign) event.getBlock().getState();

            if (sign.getLine(0).equals(
                    "" + ChatColor.GRAY + ChatColor.BOLD + "[" + ChatColor.YELLOW + "BANCO"
                            + ChatColor.GRAY + "]")
                    && !event.getPlayer().hasPermission("moneypayout.atm.create"))
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamage(BlockDamageEvent event) {
        Sign sign;
        if (!event.isCancelled()
                && event.getBlock() != null
                && (event.getBlock().getType() == Material.WALL_SIGN || event
                .getBlock().getType() == Material.SIGN_POST)) {
            sign = (Sign) event.getBlock().getState();

            if (sign.getLine(0).equals(
                    "" + ChatColor.GRAY + ChatColor.BOLD + "[" + ChatColor.YELLOW + "BANCO"
                            + ChatColor.GRAY + "]")
                    && !event.getPlayer().hasPermission("moneypayout.atm.create"))
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Block block = event.getClickedBlock();

        if (!event.isCancelled() && block != null)
            if (event.getAction() == Action.LEFT_CLICK_BLOCK
                    || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (block.getType() == Material.STONE_BUTTON
                        || block.getType() == Material.WOOD_BUTTON
                        || block.getType() == Material.LEVER) {
                    for (final BlockFace bf : BlockFace.values())
                        if (testSign(event.getPlayer(), block.getRelative(bf)))
                            return;
                } else if (block.getType() == Material.WALL_SIGN
                        || block.getType() == Material.SIGN_POST)
                    testSign(event.getPlayer(), block);
            } else if (event.getAction() == Action.PHYSICAL
                    && (block.getType() == Material.STONE_PLATE || block.getType() == Material.WOOD_PLATE))
                for (final BlockFace bf : BlockFace.values())
                    if (testSign(event.getPlayer(), block.getRelative(bf)))
                        return;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[BANCO]") && event.getPlayer().hasPermission("moneypayout.atm.create")) {
            event.setLine(0, "" + ChatColor.GRAY + ChatColor.BOLD + "["
                    + ChatColor.YELLOW + "BANCO" + ChatColor.GRAY + "]");
        }
    }

    private boolean testSign(Player player, Block block) {
        Sign sign = null;
        if (block.getType() == Material.WALL_SIGN
                || block.getType() == Material.SIGN_POST)
            sign = (Sign) block.getState();

        if (sign != null)
            if (sign.getLine(0).equals(
                    "" + ChatColor.GRAY + ChatColor.BOLD + "[" + ChatColor.YELLOW + "BANCO"
                            + ChatColor.GRAY + "]")) {
                if (player.hasPermission("moneypayout.atm.use")) {
                    final double result = plugin.cashOut(player);
                    Set<String> groups = plugin.getConfig().getConfigurationSection("Groups").getKeys(false);
                    String group = "default";
                    for (String groupEntry: groups) {
                        if (player.hasPermission("moneypayout.group." +groupEntry)) {
                            group = groupEntry;
                            break;
                        }
                    }
                    final double val = plugin.getConfig().getDouble("Group." + group + ".MoneyPerMinute", -1);

                    String msg = ChatColor.YELLOW
                            + AbstractCommand.PREFIX
                            + ChatColor.GOLD
                            + "Cashed out %MONEY% to your account. You earn that for playing for %TIME% minutes.";
                    msg = msg.replace("%MONEY%", plugin.getEconomy().format(result));
                    msg = msg.replace("%TIME%", (int) (result / val) + "");
                    player.sendMessage(msg);
                }
                return true;
            }
        return false;
    }
}
