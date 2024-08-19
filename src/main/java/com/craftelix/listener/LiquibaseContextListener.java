package com.craftelix.listener;

import com.craftelix.util.ConnectionManager;
import com.craftelix.util.PropertiesUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;

@WebListener
public class LiquibaseContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            try (Connection connection = ConnectionManager.open()) {
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                Liquibase liquibase = new Liquibase(PropertiesUtil.get("changeLogFile"), new ClassLoaderResourceAccessor(), database);
                liquibase.update("");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
