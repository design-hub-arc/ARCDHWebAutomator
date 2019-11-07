package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class RequisitionPage extends AbstractPageTemplate{

    public RequisitionPage() {
        super("https://psreports.losrios.edu/REQ_History.asp", "https://psreports.losrios.edu/REQ_HistoryQ.asp");
    }

    @Override
    public void inputQuery(String query) {
        String[] params = query.split(",");
        WebDriver driver = getDriver();
        driver.findElement(By.name("REQUESTOR_ID")).sendKeys(params[0]);
        driver.findElement(By.name("REQ_NO")).sendKeys(params[1]);
        driver.findElement(By.name("Query")).click();
    }

    @Override
    public String readQueryResult() {
        WebDriver d = getDriver();
        
        //expand if we have not yet expanded
        WebElement a = d.findElement(By.xpath("//a[@href=\"/REQ_HistoryQ.asp?REQ_History_PagingMove=ALL\""));
        
        WebElement e = d.findElement(By.xpath("//table[@border=1]"));
        HtmlTable t = new HtmlTable(e);
        return t.toCsv();
    }

    @Override
    public void afterReadingQuery() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
