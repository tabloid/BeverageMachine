package com.hvo.cli;

import com.hvo.currency.Banknote;
import com.hvo.currency.Coin;
import com.hvo.currency.Currency;
import com.hvo.exceptions.OperationIsNotSupportedException;
import com.hvo.products.BeverageProductEnum;
import com.hvo.products.Product;
import com.hvo.tradingmachines.TradingMachine;
import com.hvo.tradingmachines.TradingMachineFactory;

import java.util.*;

public class CommandLineApp {
    private TradingMachine machine;
    private Scanner scanner;

    public CommandLineApp() {
        machine = TradingMachineFactory.getBeverageMachine();
        scanner = new Scanner(System.in);
        fillMachine();
    }

    public void start() {
        printSupportedCommands();
        printSupportedCurrency();
        printAvailableProducts();
        boolean flag = true;
        while (flag) {
            String line = scanner.nextLine();
            Optional<String> optionalCommand = parseCommand(line);
            if (optionalCommand.isPresent()) {
                switch (optionalCommand.get()) {
                    case "QUIT":
                        flag = false;
                        break;
                    case "STATISTIC":
                        printStatistic();
                        break;
                    case "FILL":
                        fillMachine();
                        printStatistic();
                        break;
                    case "CANCEL":
                        pressCancel();
                        break;
                    case "ADD":
                        printMessage("Enter currency type:");
                        line = scanner.nextLine();
                        addCurrencyToMachine(line);
                        printStatistic();
                        break;
                    case "BUY":
                        printMessage("Enter product type:");
                        line = scanner.nextLine();
                        purchaseProduct(line);
                        printStatistic();
                        break;
                    default:
                        printMessage("Command is not supported.");
                }
            }
        }
    }

    private void printMessage(String str) {
        System.out.println(str);
    }

    private void fillMachine() {
        List<Currency> currencyList = new LinkedList<>();
        List<Product> productList = new LinkedList<>();
        for (int i = 0; i < 1; i++) {
            currencyList.add(Coin.QUARTER);
            currencyList.add(Coin.HALF);
            currencyList.add(Banknote.ONE);
            currencyList.add(Banknote.TWO);
            currencyList.add(Banknote.FIVE);
            currencyList.add(Banknote.TEN);
            currencyList.add(Banknote.TWENTY);
            productList.add(BeverageProductEnum.TEA);
            productList.add(BeverageProductEnum.SODA);
            productList.add(BeverageProductEnum.PEPSI);
            productList.add(BeverageProductEnum.COKE);
            productList.add(BeverageProductEnum.COFFEE);
        }
        machine.fillWithMoney(currencyList);
        machine.fillWithProducts(productList);
    }

    private void printStatistic() {
        printLine();
        System.out.println("Balance : " + machine.getBalance());
        printAvailableProducts();
        System.out.println();
        System.out.println("Available currency : " + machine.currencySet());
        printLine();
    }

    private void printAvailableProducts() {
        printLine();
        System.out.print("Available products : ");
        machine.productSet().forEach(p -> {
            System.out.print(" " + p + "[");
            System.out.print(p.getPrice() + "] ");
        });
        System.out.println();
        printLine();
    }

    private void printSupportedCurrency() {
        printLine();
        System.out.println("Supported coins : " + Arrays.toString(Coin.values()));
        System.out.println("Supported banknotes : " + Arrays.toString(Banknote.values()));
        printLine();
    }

    private void printSupportedCommands() {
        printLine();
        HashSet<String> set = new HashSet<>();
        set.add("ADD");
        set.add("BUY");
        set.add("CANCEL");
        set.add("FILL");
        set.add("STATISTIC");
        set.add("QUIT");
        System.out.println("Supported commands: " + set);
        printLine();
    }

    private void printLine() {
        String line = "----------------------------------------------------------------------";
        System.out.println(line);
    }


    private Optional<String> parseCommand(String line) {
        String[] array = line.split(" ");
        String command = null;
        if (array.length > 0) {
            command = array[0].toUpperCase();
        }
        return Optional.ofNullable(command);
    }

    private void addCurrencyToMachine(String str) {
        Optional<Currency> optionalCurrency = parseCurrency(str);
        if (optionalCurrency.isPresent()) {
            machine.add(optionalCurrency.get());
        } else {
            System.out.println("Currency is not supported.");
        }
    }

    private Optional<Currency> parseCurrency(String name) {
        name = name.toUpperCase();
        Currency currency = null;
        try {
            currency = Coin.valueOf(name);
        } catch (IllegalArgumentException ex) {
        }
        try {
            currency = Banknote.valueOf(name);
        } catch (IllegalArgumentException ex) {
        }
        return Optional.ofNullable(currency);
    }

    private void purchaseProduct(String str) {
        Optional<Product> optionalProduct = parseProduct(str);
        if (optionalProduct.isPresent()) {
            try {
                List<Currency> list = machine.purchase(optionalProduct.get());
                System.out.println("Purchase is finished. Change: " + list);
            } catch (OperationIsNotSupportedException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("Product is not supported.");
        }
    }

    private Optional<Product> parseProduct(String name) {
        name = name.toUpperCase();
        Product product = null;
        try {
            product = BeverageProductEnum.valueOf(name);
        } catch (IllegalArgumentException ex) {
        }
        return Optional.ofNullable(product);
    }

    private void pressCancel() {
        printLine();
        List<Currency> list = machine.cancelPurchase();
        System.out.println("Purchase is canceled. Refund: " + list);
        printLine();
    }


}
