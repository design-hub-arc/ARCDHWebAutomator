package automationSamples;

import automationTools.AbstractPeopleSoftAutomation;
import csv.CsvFile;
import csv.CsvFileRequirements;
import csv.CsvParser;
import csv.CsvRow;
import io.FileReaderUtil;
import java.io.IOException;
import java.util.ArrayList;
import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class BlanketPurchaseOrder extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Queries the PeopleSoft Purchase Order Balance Information page to extract funds remaining in blanket purchase order accounts";
    private static final String BPO_HEADER = "BPO ID";
    private static final String[] HEADERS = new String[]{
        BPO_HEADER
    };
    
    private static final CsvFileRequirements FILE_REQ = new CsvFileRequirements(
        "Input files should be in CSV format, "
        + "and should contain at least one column, labeled 'BPO ID'",
        HEADERS
    );
    
    public BlanketPurchaseOrder(){
        super(
            "Blanket Purchase Order Balance",
            DESC,
            "https://psreports.losrios.edu/PurchaseOrderInformation.asp", 
            FILE_REQ,
            "https://psreports.losrios.edu/PurchaseOrderInformationQ.asp"
        );
    }
    
    @Override
    public void initResult(){
        try {
            CsvFile template = CsvParser.toCsvFile(FileReaderUtil.readStream(BlanketPurchaseOrder.class.getResourceAsStream("bpoTemplate.csv")));
            CsvFile result = getResultManager().getCsvFile();
            template.getHeaders().forEach((header)->result.addHeader(header));
            getResultManager().append(template.getBody());
        } catch (IOException ex) {
            reportError(ex);
        }
    }
    
    @Override
    public void inputQuery(CsvRow query) {
        awaitFindElement(By.xpath("//input[@name='PurchaseOrderNumber']")).sendKeys(query.get(BPO_HEADER));
        awaitFindElement(By.name("B1")).click();
    }

    @Override
    public ArrayList<CsvRow> readQueryResult() {
        /*
        
        The table contains the following columns:
        BUDG PER,      PO Number, Requisition Number, Requestor, Vendor, Pre Encumbrances, Encumbrances, YTD Payments, PO Total, PO Balance
        budget period, id,        number not Id,      
        
        
        since it doesn't contain Blanket Purchase order, we will need some way of converting PO Number to the BPO name
        */
        HtmlTable t = new HtmlTable(awaitFindElement(By.xpath("//table[@border=1]")));
        CsvFile tableCsv = t.toCsvFile().getSubfile(new String[]{"PO Balance", "PO Number"});
        
        // Blanket purchase order, Funds Remaining (From PS Purchase Order Balance Information ), BPO ID, Cust Number, Web Order Address, Account Log-In
        
        CsvFile result = this.getResultManager().getCsvFile();
        result.concatinateWith(tableCsv);
        //already added rows, so we don't need to return anything
        return new ArrayList<>();
    }
}
