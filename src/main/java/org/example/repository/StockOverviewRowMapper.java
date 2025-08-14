package org.example.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.StockOverview;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockOverviewRowMapper implements RowMapper<StockOverview> {
    
    private final ObjectMapper objectMapper;
    
    public StockOverviewRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public StockOverview map(ResultSet rs, StatementContext ctx) throws SQLException {
        try {
            String jsonData = rs.getString("data");
            return objectMapper.readValue(jsonData, StockOverview.class);
        } catch (Exception e) {
            throw new SQLException("Failed to deserialize StockOverview from JSON", e);
        }
    }
}