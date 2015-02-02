package com.greatmancode.moneypayout.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Tools {
    public static void closeJDBCConnection(Connection connection) {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
        }
    }

    public static void closeJDBCStatement(PreparedStatement statement) {
        try {
            if (statement != null) statement.close();
        } catch (SQLException e) {

        }
    }

    public static boolean isValidDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {

        }
        return false;
    }
}
