package automationSamples;

import automationTools.AbstractPeopleSoftAutomation;
import io.CsvFileRequirements;
import io.FileReaderUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class BlanketPurchaseOrder extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Queries the PeopleSoft Purchase Order Balance Information page to extract funds remaining in blanket purchase order accounts";
    private static final String[] HEADERS = new String[]{
        "BPO ID"
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
            String template = FileReaderUtil.readStream(BlanketPurchaseOrder.class.getResourceAsStream("bpoTemplate.csv"));
            getResultManager().append(template);
        } catch (IOException ex) {
            reportError(ex);
        }
    }
    
    @Override
    public void inputQuery(String query) {
        String[] params = query.split(",");
        awaitFindElement(By.xpath("//input[@name='PurchaseOrderNumber']")).sendKeys(params[0]);
        awaitFindElement(By.name("B1")).click();
    }

    @Override
    public String readQueryResult() {
        HtmlTable t = new HtmlTable(awaitFindElement(By.xpath("//table[@border=1]")));
        String resultText = "";
        // Blanket purchase order, Funds Remaining (From PS Purchase Order Balance Information ), BPO ID, Cust Number, Web Order Address, Account Log-In
        String tableCsv = t.toCsv(new String[]{"PO Balance"});
        //will need to add columns for other pieces of data
        
        String[] tableRows = tableCsv.split("\\n");
        for(String row : tableRows){
            resultText += ("," + row + ",,,,\n"); //everything is empty right now except for Funds remaining.
        }
        return resultText;
    }
}
