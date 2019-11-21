package automations;

import io.CsvParser;
import io.FileRequirements;
import io.FileType;
import java.util.Arrays;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class PurchaseOrderAutomation extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Queries the PeopleSoft purchase order history page.";
    private static final String[] HEADERS = new String[]{
        "Req ID",
        "PO ID"
    };
    
    private static final FileRequirements FILE_REQ = new FileRequirements(
        "Input file should be the file downloaded from running the PSReports Requisitions automation", FileType.CSV
    );
    
    public PurchaseOrderAutomation() {
        super(
            "PSReports Purchase Order",
            DESC,
            "https://psreports.losrios.edu/PO_History.asp", 
            "https://psreports.losrios.edu/PO_HistoryQ.asp"
        );
        setFileReq(FILE_REQ);
    }

    @Override
    public void inputQuery(String query) {
        String[] params = query.split(",");
        awaitFindElement(By.name("PO_ID_Input")).sendKeys(params[1]);
        awaitFindElement(By.name("Query")).click();
    }

    @Override
    public String readQueryResult() {
        WebDriver driver = getDriver();
        String url = driver.getCurrentUrl();
        writeOutput("URL is " + url);
        
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
        writeOutput((expand) ? "I should probably expand this." : "Don't bother expanding");
        HtmlTable t = new HtmlTable(awaitFindElement(By.xpath("//table[@border=1]")));
        return t.toCsv();
    }

    @Override
    public void afterReadingQuery() {
        awaitFindElement(By.xpath("//a[@href='PO_history.asp']")).click();
    }

    @Override
    public String formatFile(String fileText) {
        return new CsvParser(HEADERS).reformat(fileText, false);
    }
    
}
