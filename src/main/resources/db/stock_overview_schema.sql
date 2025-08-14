-- Create stock_overview table
-- Execute this script manually in your PostgreSQL database

CREATE TABLE IF NOT EXISTS stock_overview (
    symbol VARCHAR(10) PRIMARY KEY,
    data JSONB NOT NULL,
    last_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on last_updated_at for better performance when ordering
CREATE INDEX IF NOT EXISTS idx_stock_overview_last_updated 
ON stock_overview(last_updated_at);

-- Create index on JSONB data for better query performance
CREATE INDEX IF NOT EXISTS idx_stock_overview_data_gin 
ON stock_overview USING GIN(data);

select * from stock_overview;