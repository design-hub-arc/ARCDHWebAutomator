package automationSamples;

import automationTools.AbstractPeopleSoftAutomation;
import csv.CsvFile;
import csv.CsvFileRequirements;
import csv.CsvRow;
import java.util.ArrayList;
import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class PurchaseOrderInfoAutomation extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Querys the PeopleSoft purchase order information page";
    private static final String REQ_HEADER = "Req ID";
    private static final String PO_HEADER = "PO ID";
    private static final String[] HEADERS = new String[]{
        REQ_HEADER,
        PO_HEADER
    };
    
    private static final CsvFileRequirements FILE_REQ = new CsvFileRequirements(
        "Input file should be the file downloaded from running the PSReports Requisitions automation", HEADERS
    );
    
    public PurchaseOrderInfoAutomation() {
        super(
            "PSReports Purchase Order Information",
            DESC,
            "https://psreports.losrios.edu/PurchaseOrderInformation.asp", 
            FILE_REQ,
            "https://psreports.losrios.edu/PurchaseOrderInformationQ.asp"
        );
    }

    @Override
    public void inputQuery(CsvRow query) {
        awaitFindElement(By.xpath("//input[@name='PurchaseOrderNumber']")).sendKeys(query.get(PO_HEADER));
        awaitFindElement(By.xpath("//input[@name='RequisitionNumber']")).sendKeys(query.get(REQ_HEADER));
        awaitFindElement(By.name("B1")).click();
    }

    @Override
    public ArrayList<CsvRow> readQueryResult() {
        HtmlTable t = new HtmlTable(awaitFindElement(By.xpath("//table[@border=1]")));
        CsvFile tableCsv = t.toCsvFile();
        CsvFile result = getResultManager().getCsvFile();
        result.concatinateWith(tableCsv);
        //already added rows, so we don't need to return anything
        return new ArrayList<>();
    }
}
