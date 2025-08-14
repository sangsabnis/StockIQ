package org.example.repository;

import org.example.model.StockOverview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StockOverviewRepositoryIntegrationTest extends BaseRepositoryIntegrationTest {

    private StockOverviewRepository repository;

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        repository = new StockOverviewRepository(jdbi, objectMapper);
    }

    @Test
    void testSaveAndFindBySymbol() {
        StockOverview testStock = createTestStock("AAPL", "Apple Inc.");
        
        repository.save(testStock);
        
        Optional<StockOverview> found = repository.findBySymbol("AAPL");
        
        assertTrue(found.isPresent());
        assertEquals("AAPL", found.get().getSymbol());
        assertEquals("Apple Inc.", found.get().getName());
        assertEquals("Technology", found.get().getSector());
        assertEquals(0, new BigDecimal("25.5").compareTo(found.get().getPeRatio()));
    }

    @Test
    void testFindBySymbolNotFound() {
        Optional<StockOverview> found = repository.findBySymbol("NONEXISTENT");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testSaveUpdateExistingStock() {
        StockOverview originalStock = createTestStock("MSFT", "Microsoft Corporation");
        repository.save(originalStock);
        
        StockOverview updatedStock = createTestStock("MSFT", "Microsoft Corp");
        updatedStock.setPeRatio(new BigDecimal("30.0"));
        repository.save(updatedStock);
        
        Optional<StockOverview> found = repository.findBySymbol("MSFT");
        
        assertTrue(found.isPresent());
        assertEquals("Microsoft Corp", found.get().getName());
        assertEquals(0, new BigDecimal("30.0").compareTo(found.get().getPeRatio()));
    }

    @Test
    void testFindAll() {
        repository.save(createTestStock("AAPL", "Apple Inc."));
        repository.save(createTestStock("MSFT", "Microsoft Corporation"));
        repository.save(createTestStock("GOOGL", "Alphabet Inc."));
        
        List<StockOverview> allStocks = repository.findAll();
        
        assertEquals(3, allStocks.size());
        
        boolean foundAAPL = allStocks.stream().anyMatch(s -> "AAPL".equals(s.getSymbol()));
        boolean foundMSFT = allStocks.stream().anyMatch(s -> "MSFT".equals(s.getSymbol()));
        boolean foundGOOGL = allStocks.stream().anyMatch(s -> "GOOGL".equals(s.getSymbol()));
        
        assertTrue(foundAAPL);
        assertTrue(foundMSFT);
        assertTrue(foundGOOGL);
    }

    @Test
    void testDeleteBySymbol() {
        repository.save(createTestStock("TSLA", "Tesla Inc."));
        
        assertTrue(repository.findBySymbol("TSLA").isPresent());
        
        boolean deleted = repository.deleteBySymbol("TSLA");
        
        assertTrue(deleted);
        assertFalse(repository.findBySymbol("TSLA").isPresent());
    }

    @Test
    void testDeleteNonExistentSymbol() {
        boolean deleted = repository.deleteBySymbol("NONEXISTENT");
        
        assertFalse(deleted);
    }

    @Test
    void testGetLastUpdatedTime() {
        repository.save(createTestStock("NVDA", "NVIDIA Corporation"));
        
        Optional<Timestamp> lastUpdated = repository.getLastUpdatedTime("NVDA");
        
        assertTrue(lastUpdated.isPresent());
        assertTrue(lastUpdated.get().toLocalDateTime().isAfter(LocalDateTime.now().minusMinutes(1)));
    }

    @Test
    void testGetLastUpdatedTimeNonExistent() {
        Optional<Timestamp> lastUpdated = repository.getLastUpdatedTime("NONEXISTENT");
        
        assertFalse(lastUpdated.isPresent());
    }

    @Test
    void testIsDataStale() {
        repository.save(createTestStock("AMZN", "Amazon.com Inc."));
        
        assertFalse(repository.isDataStale("AMZN"));
        
        assertTrue(repository.isDataStale("NONEXISTENT"));
    }

    @Test
    void testFindBySymbolIfFresh() {
        repository.save(createTestStock("NFLX", "Netflix Inc."));
        
        Optional<StockOverview> fresh = repository.findBySymbolIfFresh("NFLX");
        assertTrue(fresh.isPresent());
        assertEquals("NFLX", fresh.get().getSymbol());
        
        Optional<StockOverview> nonExistent = repository.findBySymbolIfFresh("NONEXISTENT");
        assertFalse(nonExistent.isPresent());
    }

    @Test
    void testJsonSerializationDeserialization() {
        StockOverview originalStock = createCompleteTestStock("META", "Meta Platforms Inc.");
        
        repository.save(originalStock);
        
        Optional<StockOverview> retrieved = repository.findBySymbol("META");
        
        assertTrue(retrieved.isPresent());
        StockOverview stock = retrieved.get();
        
        assertEquals(0, new BigDecimal("1.5").compareTo(stock.getPegRatio()));
        assertEquals(0, new BigDecimal("0.12").compareTo(stock.getReturnOnAssetsTTM()));
        assertEquals(0, new BigDecimal("0.25").compareTo(stock.getProfitMargin()));
        assertEquals(0, new BigDecimal("0.30").compareTo(stock.getOperatingMarginTTM()));
        assertEquals(0, new BigDecimal("0.5").compareTo(stock.getDebtToEquityRatio()));
        assertEquals(0, new BigDecimal("1.8").compareTo(stock.getQuickRatio()));
        assertEquals(0, new BigDecimal("0.15").compareTo(stock.getQuarterlyRevenueGrowthYOY()));
        assertEquals(0, new BigDecimal("0.20").compareTo(stock.getQuarterlyEarningsGrowthYOY()));
    }
}