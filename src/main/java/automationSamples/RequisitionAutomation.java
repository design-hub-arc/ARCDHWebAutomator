package automationSamples;

import automationTools.QueryManager;
import automationTools.AbstractPeopleSoftAutomation;
import io.CsvFileRequirements;
import java.util.Arrays;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class RequisitionAutomation extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Queries the PeopleSoft requistion history page.";
    private static final String[] HEADERS = new String[]{
        "requestor ID",
        "requisition number"
    };
    
    private static final CsvFileRequirements FILE_REQ = new CsvFileRequirements(
        "Input file should be in CSV format, with the following columns: " + Arrays.toString(HEADERS), HEADERS
    );
    
    public RequisitionAutomation() {
        super(
            "PSReports Requisitions",
            DESC,
            "https://psreports.losrios.edu/REQ_HistoryQ.asp",
            new QueryManager("https://psreports.losrios.edu/REQ_History.asp", FILE_REQ)
        );
    }

    @Override
    public void inputQuery(String query) {
        String[] params = query.split(",");
        awaitFindElement(By.name("REQUESTOR_ID")).sendKeys(params[0]);
        awaitFindElement(By.name("REQ_NO")).sendKeys(params[1]);
        awaitFindElement(By.name("Query")).click();
    }

    @Override
    public String readQueryResult() {
        WebDriver d = getDriver();
        String url = d.getCurrentUrl();
        writeOutput("URL is " + url);
        
        //expand if we have not yet expanded
        boolean expand = false;
        if(url.indexOf('?') == -1){
            //expanding adds a paramert to the URL. If we have no parameters, we haven't expanded
            expand = true;
        } else {
            String[] qrCodeParams = url.split("\\?")[1].split("&");
            expand = !Arrays.stream(qrCodeParams).anyMatch((String pair)->{
                return pair.equalsIgnoreCase("REQ_History_PagingMove=ALL");
            }); //expand if we have parameters, but none are the one we want
            
        }
        if(expand){
            awaitFindElement(By.xpath("//a[@href='/REQ_HistoryQ.asp?REQ_History_PagingMove=ALL']")).click();
        }
        writeOutput((expand) ? "I should probably expand this." : "Don't bother expanding");
        
        WebElement e = awaitFindElement(By.xpath("//table[@border=1]"));
        HtmlTable t = new HtmlTable(e);
        return t.toCsv();
    }
}
