package com.hvo.tradingmachines;

import com.hvo.currency.Banknote;
import com.hvo.currency.Coin;
import com.hvo.currency.Currency;
import com.hvo.exceptions.OperationIsNotSupportedException;
import com.hvo.exceptions.RepositoryException;
import com.hvo.products.Product;
import com.hvo.repositories.Repository;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractTradingMachine implements TradingMachine {
    private Logger logger = Logger.getLogger(AbstractTradingMachine.class.getName());
    private static final String ADDED_TO_BALANCE_MESSAGE = " is added to balance.";
    private static final String REMOVED_FROM_BALANCE_MESSAGE = " is removed from balance.";
    private static final String IS_PURCHASED_MESSAGE = " is purchased.";
    private static final String NOT_ENOUGH_ITEM_PATTERN = "Not enough {0} on the balance.";

    private Optional<Repository<Coin>> optionalCoinRepository;
    private Optional<Repository<Banknote>> optionalBanknoteRepository;
    private Repository<Product> productRepository;

    private int balance = 0;

    protected AbstractTradingMachine(Repository<Coin> coinRepository,
                                     Repository<Banknote> banknoteRepository,
                                     Repository<Product> productRepository) {
        this.optionalCoinRepository = Optional.ofNullable(coinRepository);
        this.optionalBanknoteRepository = Optional.ofNullable(banknoteRepository);
        this.productRepository = productRepository;
    }

    @Override
    public Set<Product> productSet() {
        return productRepository.itemSet();
    }

    @Override
    public Set<Currency> currencySet() {
        Set<Currency> set = new TreeSet<>((c1, c2) -> c2.getNominal() - c1.getNominal());
        optionalCoinRepository.ifPresent(r -> set.addAll(r.itemSet()));
        optionalBanknoteRepository.ifPresent(r -> set.addAll(r.itemSet()));
        return set;
    }

    @Override
    public void fillWithProducts(List<Product> productList) {
        try {
            for (Product product : productList) {
                productRepository.add(product);
            }
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    @Override
    public void fillWithMoney(List<Currency> currencyList) {
        addCurrencyToRepositories(currencyList);
    }

    @Override
    public void add(Currency currency) {
        //workaround :(
        List<Currency> tmp = new LinkedList<>();
        tmp.add(currency);
        addCurrencyToRepositories(tmp);
        balance = balance + currency.getNominal();
        logger.info(currency + ADDED_TO_BALANCE_MESSAGE);
    }

    @Override
    public List<Currency> cancelPurchase() {
        List<Currency> currencyList = calculateCurrencyList(balance);
        int sum = currencyList.stream().mapToInt(c -> c.getNominal()).sum();
        removeFromBalance(sum);
        return currencyList;
    }

    private void removeFromBalance(int n) {
        balance = balance - n;
        logger.info(n + REMOVED_FROM_BALANCE_MESSAGE);
    }

    @Override
    public int getBalance() {
        return balance;

    }

    @Override
    public List<Currency> purchase(Product product) throws OperationIsNotSupportedException {
        List<Currency> changeList = new LinkedList<>();
        try {
            if (product.getPrice() <= getBalance()) {
                if (productRepository.itemSet().contains(product)) {
                    removeFromBalance(product.getPrice());
                    productRepository.remove(product);
                    logger.info(product + IS_PURCHASED_MESSAGE);
                    changeList = calculateCurrencyList(getBalance());
                } else {
                    String msg = MessageFormat.format(NOT_ENOUGH_ITEM_PATTERN, "product");
                    logger.warning(msg);
                    throw new OperationIsNotSupportedException(msg);
                }
            } else {
                String msg = MessageFormat.format(NOT_ENOUGH_ITEM_PATTERN, "currency");
                logger.warning(msg);
                throw new OperationIsNotSupportedException(msg);
            }
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        int sum = changeList.stream().mapToInt(c -> c.getNominal()).sum();
        removeFromBalance(sum);
        removeCurrencyFromRepositories(changeList);
        return changeList;
    }

    private List<Currency> calculateCurrencyList(int change) {
        List<Currency> changeList = new LinkedList<>();
        Set<Currency> set = this.currencySet();
        for (Currency currency : set) {
            if (change == 0) {
                break;
            }
            int quantity = 0;
            if (isValid(optionalBanknoteRepository) && currency instanceof Banknote) {
                quantity = optionalBanknoteRepository.get().getQuantity((Banknote) currency);
            }
            if (isValid(optionalCoinRepository) && currency instanceof Coin) {
                quantity = optionalCoinRepository.get().getQuantity((Coin) currency);
            }
            int lcd = findLargestCommonDivider(change, currency, quantity);
            for (int i = 1; i <= lcd; i++) {
                changeList.add(currency);
                change = change - currency.getNominal();
            }
        }
        return changeList;
    }

    private int findLargestCommonDivider(int change, Currency currency, int quantity) {
        int i = 0;
        while (i < quantity) {
            i++;
            if (change / (i * currency.getNominal()) == 0) {
                i--;
                break;
            }
        }
        return i;
    }


    private void addCurrencyToRepositories(List<Currency> currencyList) {
        try {
            for (Currency currency : currencyList) {
                if (isValid(optionalCoinRepository) && currency instanceof Coin) {
                    optionalCoinRepository.get().add((Coin) currency);
                } else if (isValid(optionalBanknoteRepository) && currency instanceof Banknote) {
                    optionalBanknoteRepository.get().add((Banknote) currency);
                }
            }
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void removeCurrencyFromRepositories(List<Currency> currencyList) {
        try {
            for (Currency currency : currencyList) {
                if (isValid(optionalCoinRepository) && currency instanceof Coin) {
                    optionalCoinRepository.get().remove((Coin) currency);
                } else if (isValid(optionalBanknoteRepository) && currency instanceof Banknote) {
                    optionalBanknoteRepository.get().remove((Banknote) currency);
                }
            }
        } catch (RepositoryException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private <T> boolean isValid(Optional<Repository<T>> optionalRepository) {
        return optionalRepository.isPresent() && !optionalRepository.get().isFull();
    }
}
