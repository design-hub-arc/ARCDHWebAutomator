package automations;

import io.CsvParser;
import java.util.Arrays;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class PurchaseOrderAutomation extends AbstractAutomation{
    private static final String[] HEADERS = new String[]{
        "Req ID",
        "PO ID"
    } ;
    
    public PurchaseOrderAutomation() {
        super(
            "https://psreports.losrios.edu/PO_History.asp", 
            "https://psreports.losrios.edu/PO_HistoryQ.asp"
        );
    }

    @Override
    public void inputQuery(String query) {
        String[] params = query.split(",");
        getDriver().findElement(By.name("PO_ID_Input")).sendKeys(params[1]);
        getDriver().findElement(By.name("Query")).click();
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
            driver.findElement(By.xpath("//a[@href='/PO_HistoryQ.asp?POID_History_PagingMove=ALL']")).click();
        }
        writeOutput((expand) ? "I should probably expand this." : "Don't bother expanding");
        HtmlTable t = new HtmlTable(driver.findElement(By.xpath("//table[@border=1]")));
        return t.toCsv();
    }

    @Override
    public void afterReadingQuery() {
        getDriver().findElement(By.xpath("//a[@href='PO_history.asp']")).click();
    }

    @Override
    public String formatFile(String fileText) {
        return new CsvParser(HEADERS).reformat(fileText, true);
    }
    
}
