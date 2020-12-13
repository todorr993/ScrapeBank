package com.company;

import java.util.List;

//CurrencyStockRecord class represents one row from Currency table

public class CurrencyStockRecord {
    private String currencyName;
    private String pubTime;
    private String buyingRate;
    private String cashBuyingRate;
    private String sellingRate;
    private String cashSellingRate;
    private String middleRate;


    public CurrencyStockRecord(String currencyName, String buyingRate, String cashBuyingRate, String sellingRate,
                               String cashSellingRate, String middleRate, String pubTime){
        this.currencyName=currencyName;
        this.buyingRate=buyingRate;
        this.cashBuyingRate=cashBuyingRate;
        this.sellingRate=sellingRate;
        this.cashSellingRate=cashSellingRate;
        this.middleRate=middleRate;
        this.pubTime=pubTime;
    }


    public CurrencyStockRecord(List<String> record){
        if(record.size()==7){
            this.currencyName=record.get(0);
            this.buyingRate=record.get(1);
            this.cashBuyingRate=record.get(2);
            this.sellingRate=record.get(3);
            this.cashSellingRate=record.get(4);
            this.middleRate=record.get(5);
            this.pubTime=record.get(6);
        }
    }


    public String toString(String delimiter) {
        return  currencyName+delimiter+buyingRate+delimiter+cashBuyingRate+delimiter+sellingRate+delimiter+cashSellingRate
                +delimiter+middleRate+delimiter+pubTime;
    }
}
