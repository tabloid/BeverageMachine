package com.hvo.currency;

public enum Banknote implements Currency {
    ONE(1_00),
    TWO(2_00),
    FIVE(5_00),
    TEN(10_00),
    TWENTY(20_00);
    private int nominal;


    Banknote(int nominal) {
        this.nominal = nominal;
    }

    @Override
    public int getNominal() {
        return nominal;
    }
}
