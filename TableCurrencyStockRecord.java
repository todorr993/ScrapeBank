package com.company;

import java.util.LinkedList;
import java.util.List;

//class represent Table: has header and body=table rows
//provides method for adding columns and rows

public class TableCurrencyStockRecord {
    private LinkedList<String> header;
    private LinkedList<CurrencyStockRecord> body;

    public TableCurrencyStockRecord(){
        header=new LinkedList<>();
        body=new LinkedList<>();
    }

    //add new column in header
    public void addHeaderElement(String s){
        header.add(s);
    }//end method

    //add new row in table body
    public void addBodyElement(CurrencyStockRecord record){
        body.add(record);
    }//end method

    public List<String> getHeader() {
        return header;
    }//end method

    public List<CurrencyStockRecord> getBody() {
        return body;
    }//end method

}
