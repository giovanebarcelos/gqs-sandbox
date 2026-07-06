package com.tdd.tddmock.stockmanager;

import java.util.List;

public interface StockService {
    List<Stock> getAllStocks();
    Stock getStockBySymbol(String symbol);
    // Other methods for stock service...
}

