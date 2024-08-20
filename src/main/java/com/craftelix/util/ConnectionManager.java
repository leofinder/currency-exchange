package com.craftelix.util;

import com.craftelix.exception.DatabaseOperationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

    static {
        loadDriver();
    }

    private static void loadDriver() {
        try {
            Class.forName(PropertiesUtil.get("driver-class-name"));
        } catch (ClassNotFoundException e) {
            throw new DatabaseOperationException(e);
        }
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection(PropertiesUtil.get("url"));
        } catch (SQLException e) {
            throw new DatabaseOperationException(e);
        }
    }

    private ConnectionManager() {

    }
}
