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

public class DatabaseConfig {
    
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/stock_iq";
    private static final String DB_USERNAME = System.getenv("DB_USERNAME");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    
    private static HikariDataSource dataSource;
    private static Jdbi jdbi;
    
    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USERNAME);
            config.setPassword(DB_PASSWORD);
            config.setDriverClassName("org.postgresql.Driver");
            
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
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