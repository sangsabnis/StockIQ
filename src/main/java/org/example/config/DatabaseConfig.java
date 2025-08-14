package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.repository.StockOverviewRowMapper;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    
    private static final Properties properties = loadProperties();
    
    // Database configuration
    private static final String DB_URL = properties.getProperty("db.url");
    private static final String DB_USERNAME = properties.getProperty("db.username");
    private static final String DB_PASSWORD = properties.getProperty("db.password");
    private static final String DB_DRIVER = properties.getProperty("db.driver");
    
    // HikariCP configuration
    private static final int MAX_POOL_SIZE = Integer.parseInt(
            properties.getProperty("db.pool.maxPoolSize"));
    private static final int MIN_IDLE = Integer.parseInt(
            properties.getProperty("db.pool.minIdle"));
    private static final long CONNECTION_TIMEOUT = Long.parseLong(
            properties.getProperty("db.pool.connectionTimeout"));
    private static final long IDLE_TIMEOUT = Long.parseLong(
            properties.getProperty("db.pool.idleTimeout"));
    private static final long MAX_LIFETIME = Long.parseLong(
            properties.getProperty("db.pool.maxLifetime"));
    
    private static HikariDataSource dataSource;
    private static Jdbi jdbi;
    
    public static DataSource getDataSource() {
        if (dataSource == null) {
            validateConfiguration();
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USERNAME);
            config.setPassword(DB_PASSWORD);
            config.setDriverClassName(DB_DRIVER);
            
            config.setMaximumPoolSize(MAX_POOL_SIZE);
            config.setMinimumIdle(MIN_IDLE);
            config.setConnectionTimeout(CONNECTION_TIMEOUT);
            config.setIdleTimeout(IDLE_TIMEOUT);
            config.setMaxLifetime(MAX_LIFETIME);
            
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }
    
    private static Properties loadProperties() {
        Properties props = new Properties();
        String environment = System.getProperty("env", "default");
        String propertiesFile = "application-" + environment + ".properties";
        
        // Try environment-specific properties first
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream(propertiesFile)) {
            if (is != null) {
                props.load(is);
                return props;
            }
        } catch (IOException e) {
            // Fall back to default properties
        }
        
        // Load default properties
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new RuntimeException("application.properties not found in classpath");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
        
        return props;
    }
    
    private static void validateConfiguration() {
        if (DB_URL == null || DB_URL.isEmpty()) {
            throw new IllegalStateException("db.url property is required");
        }
        if (DB_USERNAME == null || DB_USERNAME.isEmpty()) {
            throw new IllegalStateException("db.username property is required");
        }
        if (DB_PASSWORD == null || DB_PASSWORD.isEmpty()) {
            throw new IllegalStateException("db.password property is required");
        }
    }
    
    public static Jdbi getJdbi() {
        if (jdbi == null) {
            jdbi = Jdbi.create(getDataSource())
                    .installPlugin(new SqlObjectPlugin())
                    .installPlugin(new PostgresPlugin())
                    .installPlugin(new Jackson2Plugin());
        }
        return jdbi;
    }
    
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}