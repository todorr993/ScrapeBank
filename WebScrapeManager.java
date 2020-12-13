package com.company;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;


import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedList;


//connects to the web and read tables
public class WebScrapeManager {
    final LocalDate endDay=LocalDate.now();
    final LocalDate startDay=LocalDate.now().minusDays(2);

    private WebScrapeBankOfChina client;
    private TableCurrencyStockRecord tableRecords;
    private OutputFile outputFile;
    private LinkedList<String> currencies;
    private String pathOfOutputDirectory;

    public WebScrapeManager (String pathOfOutputDirectory){
        //status message
        System.out.println("Application is connecting to the web page of BankOfChina. It takes a few seconds..");
        client=new WebScrapeBankOfChina();
        tableRecords=new TableCurrencyStockRecord();
        this.pathOfOutputDirectory=pathOfOutputDirectory;
    }


    //scrape all available data for every currency
    public void webScrape(){
        //status message
        System.out.println("It is connected. Reading data.");

        readCurrencyList();
        try {
            //submit form for every currency from the list
            for (String cur: currencies) {

                //submit input form
                submitRequest(cur);
                tableRecords=client.readAllTablePages(true);

                if (tableRecords!=null)
                    writeToFile(cur);
                else System.out.println("For currency "+cur+" there is no available data!"); //status message
            }
        }catch (ElementNotFoundException e)
        {
            System.out.println("Error while loading the page");
        }
    }//end method

    public void writeToFile(String currency){
        if(writeTable(currency))
            System.out.println("Output file for currency "+currency+" created!"); //status message
        else System.out.println("File for currency "+currency+" can not be created."); //status message
    }

    //read all currencies from the currency selection HtmlElement
    public void readCurrencyList(){
        client.findCurrencySelection();
        currencies=client.readCurrencyList();
    }//end method

    //submit input form with all necessary data: startDay, endDay, currency
    public void submitRequest(String currency){
        client.findAllInputElements();
        client.fillForm(startDay, endDay, currency);
        client.submit();
    }



    //write table to output file
    public boolean writeTable(String currency){
        //create file on provided path
        outputFile=new OutputTxtFile(Paths.get(pathOfOutputDirectory),currency+startDay.toString()+endDay.toString());

        try {
            outputFile.createFile();

            //write header data
            outputFile.writeToFile(tableRecords.getHeader());
            outputFile.writeToFile("\n");

            //write table rows
            for (CurrencyStockRecord record : tableRecords.getBody()) {
                outputFile.writeToFile(record.toString(","));
                outputFile.writeToFile("\n");
            }
            outputFile.closeConnection();

            return true;
        }catch (IOException e){
            return false;
        }
        catch (Exception e)
        {
            return false;
        }

    }

}
