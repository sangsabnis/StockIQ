package org.example.repository;

import org.example.model.StockOverview;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface StockOverviewDao {

    @SqlUpdate("""
        INSERT INTO stock_overview (symbol, data, last_updated_at)
        VALUES (:symbol, CAST(:jsonData AS jsonb), now())
        ON CONFLICT (symbol)
        DO UPDATE SET data = EXCLUDED.data, last_updated_at = EXCLUDED.last_updated_at
        """)
    void save(@Bind("symbol") String symbol, @Bind("jsonData") String jsonData);

    @SqlQuery("SELECT data FROM stock_overview WHERE symbol = :symbol")
    Optional<StockOverview> findBySymbol(@Bind("symbol") String symbol);

    @SqlQuery("SELECT data FROM stock_overview ORDER BY last_updated_at DESC")
    List<StockOverview> findAll();

    @SqlUpdate("DELETE FROM stock_overview WHERE symbol = :symbol")
    int deleteBySymbol(@Bind("symbol") String symbol);

    @SqlQuery("SELECT last_updated_at FROM stock_overview WHERE symbol = :symbol")
    Optional<Timestamp> getLastUpdatedTime(@Bind("symbol") String symbol);
}