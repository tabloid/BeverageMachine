package com.hvo.tradingmachines;

import com.hvo.currency.Currency;
import com.hvo.exceptions.OperationIsNotSupportedException;
import com.hvo.products.Product;

import java.util.List;
import java.util.Set;

public interface TradingMachine {

    public void fillWithMoney(List<Currency> currencyList);

    public void fillWithProducts(List<Product> productList);

    public void add(Currency currency);

    public List<Currency> purchase(Product product) throws OperationIsNotSupportedException;

    public List<Currency> cancelPurchase();

    public Set<Product> productSet();

    public Set<Currency> currencySet();

    public int getBalance();

}
