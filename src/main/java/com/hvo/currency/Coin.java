package com.hvo.currency;

public enum Coin implements Currency {
    QUARTER(25), HALF(50);
    private int nominal;


    Coin(int nominal) {
        this.nominal = nominal;
    }

    @Override
    public int getNominal() {
        return nominal;
    }

}
