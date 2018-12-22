package com.hvo.tradingmachines;

import com.hvo.currency.Banknote;
import com.hvo.currency.Coin;
import com.hvo.products.Product;
import com.hvo.repositories.MapRepository;

public class TradingMachineFactory {
    public static TradingMachine getBeverageMachine() {
        MapRepository<Coin> coinMapRepository = new MapRepository<>(100);
        MapRepository<Banknote> banknoteMapRepository = new MapRepository<>(1000);
        MapRepository<Product> productMapRepository = new MapRepository<>(10);
        AbstractTradingMachine beverageMachine = new AbstractTradingMachine(
                coinMapRepository,
                banknoteMapRepository,
                productMapRepository) {
        };
        return beverageMachine;
    }
}
