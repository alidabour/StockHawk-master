package com.ali.android.stockhawk.widget;

/**
 * Created by ali on 15/09/16.
 */
class Stock {
    private String symbol;

    private String bidPrice;
    private String change;
    private int isUp;
    public Stock(String symbol, String bidPrice, String change, int isUp) {
        this.symbol = symbol;
        this.bidPrice = bidPrice;
        this.change = change;
        this.isUp = isUp;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public String getChange() {
        return change;
    }

    public int isUp() {
        return isUp;
    }

    public void setUp(int up) {
        isUp = up;
    }

}
