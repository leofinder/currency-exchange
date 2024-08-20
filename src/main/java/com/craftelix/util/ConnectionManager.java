package com.craftelix.util;

import com.craftelix.exception.DatabaseOperationException;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            String url = PropertiesUtil.get("url");
            if (url.startsWith("jdbc:sqlite:")) {
                URL resource = ConnectionManager.class.getClassLoader().getResource("application.properties");
                String relativePath = url.replace("jdbc:sqlite:", "");
                Path dbPath = Paths.get(resource.toURI()).getParent().resolve(relativePath);
                url = "jdbc:sqlite:" + dbPath.toAbsolutePath();
            }
            return DriverManager.getConnection(url);
        } catch (SQLException | URISyntaxException e) {
            throw new DatabaseOperationException(e);
        }
    }

    private ConnectionManager() {

    }
}
