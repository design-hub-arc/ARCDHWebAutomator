package automationSamples;

import automationTools.AbstractPeopleSoftAutomation;
import csv.CsvFile;
import csv.CsvFileRequirements;
import csv.CsvRow;
import java.util.Arrays;
import logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class PurchaseOrderAutomation extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Queries the PeopleSoft purchase order history page.";
    private static final String REQ_HEADER = "Req ID";
    private static final String PO_HEADER = "PO ID";
    
    private static final String[] HEADERS = new String[]{
        REQ_HEADER,
        PO_HEADER
    };
    
    private static final CsvFileRequirements FILE_REQ = new CsvFileRequirements(
        "Input file should be the file downloaded from running the PSReports Requisitions automation", HEADERS
    );
    
    public PurchaseOrderAutomation() {
        super(
            "PSReports Purchase Order",
            DESC,
            "https://psreports.losrios.edu/PO_History.asp",
            FILE_REQ,
            "https://psreports.losrios.edu/PO_HistoryQ.asp"
        );
    }

    @Override
    public void inputQuery(CsvRow query) {
        awaitFindElement(By.name("PO_ID_Input")).sendKeys(query.get(PO_HEADER));
        awaitFindElement(By.name("Query")).click();
    }

    @Override
    public void readQueryResult(CsvFile saveFile) {
        WebDriver driver = getDriver();
        String url = driver.getCurrentUrl();
        Logger.log("PurchaseOrderAutomation.readQueryResult", "URL is " + url);
        
        boolean expand = false;
        if(url.indexOf('?') == -1){
            //expanding adds a paramert to the URL. If we have no parameters, we haven't expanded
            expand = true;
        } else {
            String[] qrCodeParams = url.split("\\?")[1].split("&");
            expand = !Arrays.stream(qrCodeParams).anyMatch((String pair)->{
                return pair.equalsIgnoreCase("POID_History_PagingMove=ALL");
            }); //expand if we have parameters, but none are the one we want
            
        }
        if(expand){
            awaitFindElement(By.xpath("//a[@href='/PO_HistoryQ.asp?POID_History_PagingMove=ALL']")).click();
        }
        Logger.log("PurchaseOrderAutomation.readQueryResult", (expand) ? "I should probably expand this." : "Don't bother expanding");
        HtmlTable t = new HtmlTable(awaitFindElement(By.xpath("//table[@border=1]")));
        CsvFile tableCsv = t.toCsvFile();
        saveFile.concatinateWith(tableCsv);
    }
}
