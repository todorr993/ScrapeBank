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
    public void getHtmlPage (String baseURL){
        try {
            page = client.getPage(baseURL);
        }catch(Exception e){
            System.out.println("Unable to load page");
            System.exit(0);
        }
    }//end method

    //find all form elements using XPath
    public void findAllFormElements() throws ElementNotFoundException{
        startDateInput = page.getFirstByXPath("//form//input[@name='erectDate']");
        endDateInput = page.getFirstByXPath("//form//input[@name='nothing']");
    }//end method

    //find currency selection using XPath
    public void findCurrencySelection() throws ElementNotFoundException {
       currencySelection = page.getHtmlElementById("pjname");
    }//end method

    //find currency table using XPath
    public void findCurrencyTable() throws ElementNotFoundException{
        currencyTable=page.getFirstByXPath("/html/body/table[2]");
    }//end method

    //find all elements required for page numbering using XPath
    public void findPageNumberElement() throws ElementNotFoundException{
        pageNumber= page.getFirstByXPath("//table//span[@class='nav_pagenum']");
    }//end method

    //find all elements required for page numbering using XPath
    public void findNextPageElement() throws ElementNotFoundException{
        nextPage=page.getAnchorByText("Next");
    }//end method

    //find all elements that needs to be submit
    public void findAllInputElements(){
        findAllFormElements();
        findCurrencySelection();
    }

    //fill all form elements
    public void fillForm(LocalDate startDay, LocalDate endDay, String currency){

        //filling form fields
        startDateInput.setValueAttribute(startDay.toString());
        endDateInput.setValueAttribute(endDay.toString());

        //select currency
        HtmlOption option = currencySelection.getOptionByValue(currency);
        currencySelection.setSelectedAttribute(option, true);

    }//end method

    //submit Form: first select the enclosing Form, then generate Post request
    public void submit(){
        HtmlForm form= startDateInput.getEnclosingForm();
        try {
            page=client.getPage(form.getWebRequest(null));
            //System.out.println(page.asXml());
        }catch (Exception e){
            System.out.println("Unable to load new page!");
            System.exit(0);
        }
    }//end method


    //read all options from HtmlSelection Currency
    public LinkedList<String> readCurrencyList(){
        LinkedList currencies=new LinkedList<>();

        //return all selection options
        List<HtmlOption> options=currencySelection.getOptions();
        for (HtmlOption item: options){
            currencies.add(item.asText());
        }

        //remove first element, it is not currency related
        currencies.remove(0);

        return currencies;
    }//end method

    //check if currencyTable has data
    public boolean isEmptyCurrencyTable(){
        if(currencyTable.asText().contains("sorry, no records"))
            return true;
        return false;
    }


    //iterate through table and insert cells value in tableCurrencyRecord
    public void readTablePage (boolean header, TableCurrencyStockRecord tableRecords){

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



    public TableCurrencyStockRecord readAllTablePages (boolean header) {
        try {
            TableCurrencyStockRecord tableRecords = new TableCurrencyStockRecord();
            int numberOfPages;

            //find currency table from new getPage
            findCurrencyTable();

            //check if it is empty, then return NULL
            if (isEmptyCurrencyTable())
                return null;

            //find all elements for iterating through pages
            findPageNumberElement();
            findNextPageElement();
            numberOfPages=Integer.valueOf(pageNumber.asText());  //can throw NumberFormatException

            //go through all pages
            while (numberOfPages > 0) {

                readTablePage(header, tableRecords);

                //read header only once
                if (header)
                    header=false;

                //click on next page, returns new page
                page = nextPage.click();
                //decrease numberOfPages
                numberOfPages--;

                //find currency table and Next from new getPage
                findCurrencyTable();
                findNextPageElement();
            }
            return tableRecords;

        }catch(IOException e)
        {
            return null;
        }catch (NumberFormatException e){
            return null;
        }
        catch (JavaScriptException e)
        {
            System.out.println("JavaScript Error, application is not able to read data from web page. Please run it again.");
            System.exit(-1);
            return null;
        }
        catch (Exception e){
            System.out.println("Application is not able to read data from web page. Please run it again.");
            System.exit(-1);
            return null;
        }

    }//end method


}//end class
