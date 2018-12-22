package com.hvo.products;

public enum BeverageProductEnum implements Product {
    COKE(10_00),
    PEPSI(12_00),
    SODA(8_00),
    TEA(4_00),
    COFFEE(6_00);
    private int price;

    BeverageProductEnum(int price) {
        this.price = price;
    }

    @Override
    public int getPrice() {
        return price;
    }
}
