package org.example.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.model.StockOverview;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Testcontainers
public abstract class BaseRepositoryIntegrationTest {

    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    protected DataSource dataSource;
    protected Jdbi jdbi;
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        config.setDriverClassName("org.postgresql.Driver");
        
        dataSource = new HikariDataSource(config);
        
        objectMapper = new ObjectMapper();
        
        jdbi = Jdbi.create(dataSource)
                .installPlugin(new SqlObjectPlugin())
                .installPlugin(new PostgresPlugin())
                .installPlugin(new Jackson2Plugin())
                .registerRowMapper(StockOverview.class, new StockOverviewRowMapper(objectMapper));
        
        createSchema();
    }

    @AfterEach
    void tearDown() {
        cleanupDatabase();
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }

    protected void cleanupDatabase() {
        jdbi.useHandle(handle -> {
            handle.execute("DELETE FROM stock_overview");
        });
    }

    protected void createSchema() {
        jdbi.useHandle(handle -> {
            handle.execute("""
                CREATE TABLE IF NOT EXISTS stock_overview (
                    symbol VARCHAR(10) PRIMARY KEY,
                    data JSONB NOT NULL,
                    last_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
            
            handle.execute("""
                CREATE INDEX IF NOT EXISTS idx_stock_overview_last_updated 
                ON stock_overview(last_updated_at)
                """);
            
            handle.execute("""
                CREATE INDEX IF NOT EXISTS idx_stock_overview_data_gin 
                ON stock_overview USING GIN(data)
                """);
        });
    }

    protected StockOverview createTestStock(String symbol, String name) {
        StockOverview stock = new StockOverview(symbol, name);
        stock.setExchange("NASDAQ");
        stock.setCurrency("USD");
        stock.setCountry("USA");
        stock.setSector("Technology");
        stock.setIndustry("Software");
        stock.setPeRatio(new BigDecimal("25.5"));
        stock.setPriceToBookRatio(new BigDecimal("3.2"));
        stock.setReturnOnEquityTTM(new BigDecimal("0.15"));
        stock.setCurrentRatio(new BigDecimal("2.1"));
        stock.setBeta(new BigDecimal("1.2"));
        return stock;
    }

    protected StockOverview createCompleteTestStock(String symbol, String name) {
        StockOverview stock = createTestStock(symbol, name);
        stock.setPegRatio(new BigDecimal("1.5"));
        stock.setReturnOnAssetsTTM(new BigDecimal("0.12"));
        stock.setProfitMargin(new BigDecimal("0.25"));
        stock.setOperatingMarginTTM(new BigDecimal("0.30"));
        stock.setDebtToEquityRatio(new BigDecimal("0.5"));
        stock.setQuickRatio(new BigDecimal("1.8"));
        stock.setQuarterlyRevenueGrowthYOY(new BigDecimal("0.15"));
        stock.setQuarterlyEarningsGrowthYOY(new BigDecimal("0.20"));
        return stock;
    }
}