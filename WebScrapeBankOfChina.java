package com.company;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import net.sourceforge.htmlunit.corejs.javascript.JavaScriptException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

//WebScrape class has all elements necessary for currency scraping from https://srh.bankofchina.com/search/whpj/searchen.jsp
//it identifies all HTML elements and provides method to fill and submit HtmlForm, and read from HtmlTable

public class WebScrapeBankOfChina  {

    private WebClient client;
    private HtmlPage page;
    private HtmlInput startDateInput;
    private HtmlInput endDateInput;
    private HtmlInput currencyInput;
    private HtmlInput pageNumberInput;
    private HtmlSelect currencySelection;
    private HtmlTable currencyTable;
    private HtmlElement pageNumber;
    private HtmlAnchor nextPage;


    public WebScrapeBankOfChina(){
        client=new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(true);
        //client.getOptions().setTimeout(15000);
        getHtmlPage("https://srh.bankofchina.com/search/whpj/searchen.jsp");
    }

    //client get request to server
    private void getHtmlPage (String baseURL){
        try {
            page = client.getPage(baseURL);
        }catch(Exception e){
            System.out.println("Unable to load page");
            System.exit(0);
        }
    }//end method

    //find all form elements using XPath
    private void findAllFormElements() throws ElementNotFoundException{
        startDateInput = page.getFirstByXPath("//form[@name='pageform']//input[@name='erectDate']");
        endDateInput = page.getFirstByXPath("//form[@name='pageform']//input[@name='nothing']");
        currencyInput = page.getFirstByXPath("//form[@name='pageform']//input[@name='pjname']");
        pageNumberInput=page.getFirstByXPath("//form[@name='pageform']//input[@name='page']");
    }//end method

    //find currency selection using XPath
    private void findCurrencySelection() throws ElementNotFoundException {
       currencySelection = page.getHtmlElementById("pjname");
    }//end method


    //find currency table using XPath, if there is relevant data return true
    private boolean findCurrencyTable() throws ElementNotFoundException{
        //find HTML element
        currencyTable=page.getFirstByXPath("/html/body/table[2]");

        //check if it contains currency data
        if (isEmptyCurrencyTable())
            return false;

        return true;
    }//end method


    //check if currencyTable has data
    private boolean isEmptyCurrencyTable(){
        if(currencyTable.asText().contains("sorry, no records"))
            return true;
        return false;
    }


    //find all elements required for page numbering using XPath
    private void findPageNumberElement() throws ElementNotFoundException{
        pageNumber= page.getFirstByXPath("//table//span[@class='nav_pagenum']");
    }//end method

    //fill all form elements
    public void fillForm(LocalDate startDay, LocalDate endDay, String currency, String pageNumber){
        startDateInput.setValueAttribute(startDay.toString());
        endDateInput.setValueAttribute(endDay.toString());
        currencyInput.setValueAttribute(currency);
        pageNumberInput.setValueAttribute(pageNumber);
    }//end method


    //submit Form: first select the enclosing Form, then generate Post request
    public void submit(){
        HtmlForm form= startDateInput.getEnclosingForm();
        try {
            page=client.getPage(form.getWebRequest(null));
        }catch (Exception e){
            System.out.println("Unable to load new page!");
            System.exit(0);
        }
    }//end method


    //read all options from HtmlSelection Currency
    public LinkedList<String> readCurrencyList(){
        LinkedList currencies=new LinkedList<>();

        findCurrencySelection();
        //return all selection options
        List<HtmlOption> options=currencySelection.getOptions();
        for (HtmlOption item: options){
            currencies.add(item.asText());
        }

        //remove first element, it is not currency related
        currencies.remove(0);

        return currencies;
    }//end method


    //iterate through table and insert cells value in tableCurrencyRecord
    private void readTable (boolean header, TableCurrencyStockRecord tableRecords){

        //iterate through table rows
        for(HtmlTableRow row: currencyTable.getRows()) {

            //skip first row if header=false
            if(row.getIndex()==0 && !header)
                continue;

            LinkedList<String> record=new LinkedList<>();

            //iterate through row cells
            for (HtmlTableCell cell : row.getCells()) {
                if(header){         //read header elements
                    tableRecords.addHeaderElement(cell.asText());
                }
                else{
                    record.add(cell.asText());
                }

            }

            header = false;

            //if it read header rows, then record is empty
            if(record.isEmpty())
                continue;

            tableRecords.addBodyElement(new CurrencyStockRecord(record));
        }

    }//end method


    public TableCurrencyStockRecord search (LocalDate startDay, LocalDate endDay, String currency){
        TableCurrencyStockRecord table=new TableCurrencyStockRecord();

        int numberOfPages=1;
        int page=1;

        //initialize input form elements
        findAllFormElements();

        while (numberOfPages >= page) {
            fillForm(startDay, endDay, currency, Integer.toString(page));
            submit();

            if (!findCurrencyTable())
                return null;

            if (page == 1) {
                readTable(true, table);
                findPageNumberElement();
                numberOfPages = Integer.valueOf(pageNumber.asText());
            } else {
                readTable(false, table);
            }
            page++;
        }
        return table;
    }



}//end class
