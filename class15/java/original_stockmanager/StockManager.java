package com.tdd.tddmock.stockmanager;

import java.util.List;

public class StockManager {
    private StockService stockService;

    public StockManager(StockService stockService) {
        this.stockService = stockService;
    }

    public List<Stock> getStocks() {
        return stockService.getAllStocks();
    }

    public Stock getStockBySymbol(String symbol) {
        return stockService.getStockBySymbol(symbol);
    }

    // Other methods for stock management...
}

