package automations;

import io.CsvParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class PurchaseOrderInfoAutomation extends AbstractAutomation{
    private static final String[] HEADERS = new String[]{
        "Req ID",
        "PO ID"
    };
    
    public PurchaseOrderInfoAutomation() {
        super(
            "https://psreports.losrios.edu/PurchaseOrderInformation.asp", 
            "https://psreports.losrios.edu/PurchaseOrderInformationQ.asp"
        );
    }

    @Override
    public void inputQuery(String query) {
        String[] params = query.split(",");
        WebDriver driver = getDriver();
        driver.findElement(By.xpath("//input[@name='PurchaseOrderNumber']")).sendKeys(params[1]);
        driver.findElement(By.xpath("//input[@name='RequisitionNumber']")).sendKeys(params[0]);
        driver.findElement(By.name("B1")).click();
    }

    @Override
    public String readQueryResult() {
        HtmlTable t = new HtmlTable(getDriver().findElement(By.xpath("//table[@border=1]")));
        return t.toCsv();
    }

    @Override
    public void afterReadingQuery() {
        getDriver().findElement(By.xpath("//a[@href='PurchaseOrderInformation.asp'")).click();
    }

    @Override
    public String formatFile(String fileText) {
        return new CsvParser(HEADERS).reformat(fileText);
    }
}
