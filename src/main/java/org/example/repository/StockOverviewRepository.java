package org.example.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.example.model.StockOverview;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Singleton
public class StockOverviewRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(StockOverviewRepository.class);
    private final StockOverviewDao dao;
    private final ObjectMapper objectMapper;
    
    @Inject
    public StockOverviewRepository(Jdbi jdbi, ObjectMapper objectMapper) {
        this.dao = jdbi.onDemand(StockOverviewDao.class);
        this.objectMapper = objectMapper;
    }
    
    public void save(StockOverview stockOverview) {
        try {
            String jsonData = objectMapper.writeValueAsString(stockOverview);
            dao.save(stockOverview.getSymbol(), jsonData);
            logger.info("Successfully saved/updated stock overview for symbol: {}", stockOverview.getSymbol());
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize StockOverview to JSON for symbol: {}", stockOverview.getSymbol(), e);
            throw new RuntimeException("JSON serialization failed", e);
        } catch (Exception e) {
            logger.error("Database error when saving stock overview for symbol: {}", stockOverview.getSymbol(), e);
            throw new RuntimeException("Database operation failed", e);
        }
    }
    
    public Optional<StockOverview> findBySymbol(String symbol) {
        try {
            Optional<StockOverview> result = dao.findBySymbol(symbol);
            if (result.isPresent()) {
                logger.debug("Found stock overview for symbol: {}", symbol);
            } else {
                logger.debug("No stock overview found for symbol: {}", symbol);
            }
            return result;
        } catch (Exception e) {
            logger.error("Database error when finding stock overview for symbol: {}", symbol, e);
            throw new RuntimeException("Database operation failed", e);
        }
    }
    
    public List<StockOverview> findAll() {
        try {
            List<StockOverview> stockOverviews = dao.findAll();
            logger.info("Retrieved {} stock overviews from database", stockOverviews.size());
            return stockOverviews;
        } catch (Exception e) {
            logger.error("Database error when retrieving all stock overviews", e);
            throw new RuntimeException("Database operation failed", e);
        }
    }
    
    public boolean deleteBySymbol(String symbol) {
        try {
            int rowsAffected = dao.deleteBySymbol(symbol);
            if (rowsAffected > 0) {
                logger.info("Successfully deleted stock overview for symbol: {}", symbol);
                return true;
            } else {
                logger.info("No stock overview found to delete for symbol: {}", symbol);
                return false;
            }
        } catch (Exception e) {
            logger.error("Database error when deleting stock overview for symbol: {}", symbol, e);
            throw new RuntimeException("Database operation failed", e);
        }
    }
    
    public Optional<Timestamp> getLastUpdatedTime(String symbol) {
        try {
            return dao.getLastUpdatedTime(symbol);
        } catch (Exception e) {
            logger.error("Database error when getting last updated time for symbol: {}", symbol, e);
            throw new RuntimeException("Database operation failed", e);
        }
    }
    
    public boolean isDataStale(String symbol) {
        Optional<Timestamp> lastUpdated = getLastUpdatedTime(symbol);
        if (lastUpdated.isEmpty()) {
            return true; // No data exists, so it's stale
        }
        
        LocalDateTime lastUpdatedTime = lastUpdated.get().toLocalDateTime();
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        
        boolean stale = lastUpdatedTime.isBefore(twentyFourHoursAgo);
        logger.debug("Data for symbol {} is {}stale. Last updated: {}", 
                    symbol, stale ? "" : "not ", lastUpdatedTime);
        
        return stale;
    }
    
    public Optional<StockOverview> findBySymbolIfFresh(String symbol) {
        if (isDataStale(symbol)) {
            logger.debug("Data for symbol {} is stale, returning empty", symbol);
            return Optional.empty();
        }
        return findBySymbol(symbol);
    }
}