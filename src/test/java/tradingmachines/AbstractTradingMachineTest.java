package tradingmachines;

import com.hvo.currency.Banknote;
import com.hvo.currency.Coin;
import com.hvo.currency.Currency;
import com.hvo.exceptions.OperationIsNotSupportedException;
import com.hvo.products.BeverageProductEnum;
import com.hvo.products.Product;
import com.hvo.tradingmachines.TradingMachine;
import com.hvo.tradingmachines.TradingMachineFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class AbstractTradingMachineTest {

    private List<Product> productList;
    private List<Currency> currencyList;

    @Before
    public void init() {
        productList = new LinkedList<>();
        currencyList = new LinkedList<>();
    }

    @Test
    public void fillWithProductsTest() {
        TradingMachine tradingMachine = TradingMachineFactory.getBeverageMachine();
        productList.add(BeverageProductEnum.COFFEE);
        tradingMachine.fillWithProducts(productList);
        Assert.assertTrue(tradingMachine.productSet().contains(BeverageProductEnum.COFFEE));
        Assert.assertFalse(tradingMachine.productSet().contains(BeverageProductEnum.COKE));
    }

    @Test
    public void fillWithMoneyTest() {
        TradingMachine machine = TradingMachineFactory.getBeverageMachine();
        currencyList.add(Coin.HALF);
        currencyList.add(Banknote.ONE);
        machine.fillWithMoney(currencyList);
        Set<Currency> currencySet = machine.currencySet();
        Assert.assertTrue(currencySet.contains(Coin.HALF));
        Assert.assertTrue(currencySet.contains(Banknote.ONE));
        Assert.assertFalse(currencySet.contains(Coin.QUARTER));
        Assert.assertEquals(0, machine.getBalance());
    }

    @Test
    public void addCurrencyToBalanceTest() {
        TradingMachine machine = TradingMachineFactory.getBeverageMachine();
        machine.fillWithMoney(getCurrencyList(1));
        int balanceBefore = machine.getBalance();
        machine.add(Coin.QUARTER);
        int balanceAfter = machine.getBalance();
        Assert.assertEquals(balanceBefore, balanceAfter - Coin.QUARTER.getNominal());
        List<Currency> list = machine.cancelPurchase();
        int balanceAfter2 = machine.getBalance();
        Assert.assertEquals(balanceBefore, balanceAfter2);
        Assert.assertTrue(list.contains(Coin.QUARTER));
    }

    @Test
    public void cancelPurchaseTest() {
        TradingMachine machine = TradingMachineFactory.getBeverageMachine();
        Assert.assertEquals(0, machine.getBalance());
        List<Currency> list = machine.cancelPurchase();
        Assert.assertEquals(0, machine.getBalance());
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void purchaseProductTest1() throws OperationIsNotSupportedException {
        TradingMachine machine = TradingMachineFactory.getBeverageMachine();
        machine.fillWithMoney(getCurrencyList(1));
        machine.fillWithProducts(getProductList(1));
        int balanceBefore = machine.getBalance();
        int sum = 0;
        machine.add(Banknote.ONE);
        sum += Banknote.ONE.getNominal();
        machine.add(Banknote.TWO);
        sum += Banknote.TWO.getNominal();
        machine.add(Banknote.FIVE);
        sum += Banknote.FIVE.getNominal();
        int balanceAfter = machine.getBalance();
        Assert.assertEquals(sum, balanceAfter - balanceBefore);
        List<Currency> change = machine.purchase(BeverageProductEnum.TEA);
        Assert.assertEquals(2, change.stream().filter(c -> c.equals(Banknote.TWO)).count());
    }
    @Test
    public void purchaseProductTest2() throws OperationIsNotSupportedException {
        TradingMachine machine = TradingMachineFactory.getBeverageMachine();
        machine.fillWithMoney(getCurrencyList(1));
        machine.fillWithProducts(getProductList(1));
        machine.add(Banknote.TEN);
        List<Currency> change = machine.purchase(BeverageProductEnum.COFFEE);
        Assert.assertEquals(1, change.stream().filter(c -> c.equals(Banknote.TWO)).count());
        Assert.assertEquals(1, change.stream().filter(c -> c.equals(Banknote.ONE)).count());
        Assert.assertEquals(1, change.stream().filter(c -> c.equals(Coin.HALF)).count());
        Assert.assertEquals(1, change.stream().filter(c -> c.equals(Coin.QUARTER)).count());
        Assert.assertEquals(25, machine.getBalance());
    }

    private List<Currency> getCurrencyList(int n) {
        List<Currency> tempList = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            tempList.add(Coin.QUARTER);
            tempList.add(Coin.HALF);
            tempList.add(Banknote.ONE);
            tempList.add(Banknote.TWO);
            tempList.add(Banknote.FIVE);
            tempList.add(Banknote.TEN);
            tempList.add(Banknote.TWENTY);
        }
        return tempList;
    }

    private List<Product> getProductList(int n) {
        List<Product> tempList = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            tempList.add(BeverageProductEnum.TEA);
            tempList.add(BeverageProductEnum.SODA);
            tempList.add(BeverageProductEnum.PEPSI);
            tempList.add(BeverageProductEnum.COKE);
            tempList.add(BeverageProductEnum.COFFEE);
        }
        return tempList;
    }
}
