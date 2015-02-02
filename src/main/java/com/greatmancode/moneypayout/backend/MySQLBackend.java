package com.greatmancode.moneypayout.backend;

import com.greatmancode.moneypayout.MoneyPayoutPlugin;
import com.greatmancode.moneypayout.utils.Tools;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class MySQLBackend implements Backend {

    private final MoneyPayoutPlugin plugin;
    private final HikariDataSource db;

    public MySQLBackend(MoneyPayoutPlugin plugin) throws SQLException {
        this.plugin = plugin;
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(10);
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", plugin.getConfig().getString("sql.host", "localhost"));
        config.addDataSourceProperty("port", plugin.getConfig().getInt("sql.port", 3306));
        config.addDataSourceProperty("databaseName", plugin.getConfig().getString("sql.database", "tgym"));
        config.addDataSourceProperty("user", plugin.getConfig().getString("sql.username", "root"));
        config.addDataSourceProperty("password", plugin.getConfig().getString("sql.password", ""));
        db = new HikariDataSource(config);

        Connection conn = db.getConnection();

        PreparedStatement statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS moneypayout(" +
                "uuid varchar(36) NOT NULL,money double NOT NULL DEFAULT 0, PRIMARY KEY (uuid));");
        statement.executeUpdate();
        statement.close();
        conn.close();
    }
    @Override
    public void savePlayer(OfflinePlayer p, double value) {
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = db.getConnection();
            statement = conn.prepareStatement("INSERT INTO moneypayout(uuid, money) VALUES(?,?) " +
                    "ON DUPLICATE KEY UPDATE money=VALUES(money)");
            statement.setString(1, p.getUniqueId().toString());
            statement.setDouble(2, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            Tools.closeJDBCConnection(conn);
        }
    }

    @Override
    public double retrievePlayer(OfflinePlayer p) {
        Connection conn = null;
        PreparedStatement statement = null;
        double result = 0;
        try {
            conn = db.getConnection();
            statement = conn.prepareStatement("SELECT money FROM moneypayout WHERE uuid=?");
            statement.setString(1,p.getUniqueId().toString());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                result = set.getDouble("money");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            Tools.closeJDBCConnection(conn);
        }
        return result;
    }

    @Override
    public void removePlayer(OfflinePlayer p) {
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = db.getConnection();
            statement = conn.prepareStatement("DELETE FROM moneypayout WHERE uuid=?");
            statement.setString(1, p.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            Tools.closeJDBCConnection(conn);
        }
    }

    @Override
    public void disable() {
        db.close();
    }

    @Override
    public void importPlayers(Map<String, Double> players) {
        Connection conn = null;
        PreparedStatement statement = null;
        StringBuilder builder = new StringBuilder();
        try {
            conn = db.getConnection();
            boolean first = true;
            int i = 0;
            for (Map.Entry<String, Double> entry : players.entrySet()) {

                if (!first) {
                    builder.append(",");
                } else {
                    first = false;
                    builder.append("INSERT INTO moneypayout(uuid, money) VALUES");
                }
                builder.append("('"+ Bukkit.getOfflinePlayer(entry.getKey()).getUniqueId() + "'," + entry.getValue() +")");
                i++;
                if (i == 100) {
                    i = 0;
                    first = true;
                    statement = conn.prepareStatement(builder.toString());
                    statement.executeUpdate();
                    statement.close();
                    builder = new StringBuilder();
                }
            }
            statement = conn.prepareStatement(builder.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Tools.closeJDBCStatement(statement);
            Tools.closeJDBCConnection(conn);
        }
    }
}
