package com.hvo.repositories;

import com.hvo.currency.Banknote;
import com.hvo.currency.Coin;
import com.hvo.exceptions.RepositoryException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class MapRepositoryTest {
    @Test(expected = RepositoryException.class)
    public void addToRepositoryFunctionalityTest() throws Exception {
        MapRepository<Coin> coinRepository = new MapRepository<>(3);
        coinRepository.add(Coin.QUARTER);
        Assert.assertEquals(1, coinRepository.size());
        Assert.assertTrue(coinRepository.itemSet().contains(Coin.QUARTER));
        coinRepository.add(Coin.QUARTER);
        coinRepository.add(Coin.HALF);
        Assert.assertEquals(2, coinRepository.getQuantity(Coin.QUARTER));
        Assert.assertEquals(3, coinRepository.size());
        coinRepository.add(Coin.HALF);
    }

    @Test(expected = RepositoryException.class)
    public void removeFromRepositoryFunctionalityTest() throws Exception {
        MapRepository<Banknote> banknoteRepository = new MapRepository<>(1);
        banknoteRepository.add(Banknote.FIVE);
        Set<Banknote> setBefore = banknoteRepository.itemSet();
        Assert.assertTrue(setBefore.contains(Banknote.FIVE));
        Assert.assertFalse(setBefore.contains(Banknote.ONE));
        banknoteRepository.remove(Banknote.FIVE);
        Set<Banknote> setAfter = banknoteRepository.itemSet();
        Assert.assertFalse(setAfter.contains(Banknote.FIVE));
        banknoteRepository.remove(Banknote.FIVE);
    }
}
