package pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Matt
 */
public class AccountBalancePage extends AbstractPageTemplate{
    public AccountBalancePage(){
        super(
            "https://psreports.losrios.edu/AccountBalanceSumDescr.asp",
            "https://psreports.losrios.edu/AccountBalanceSumDescrQ.asp"
        );
    }
    @Override
    public void inputQuery(String query) {
        String[] params = query.split(",");
        WebDriver driver = getDriver();
        String[] names = new String[]{
            "BusinessUnit",
            "Account",
            "Fund",
            "ORG",
            "Program",
            "SubClass",
            //"BudgetYear",
            "ProjectGrant"
        };
        for(int i = 0; i < params.length; i++){
            driver.findElement(By.name(names[i])).sendKeys(params[i]);
        }
        
        //do I want autoclick to be optional?
        driver.findElement(By.name("Query")).click();
    }

    @Override
    public String readQueryResult() {
        StringBuilder ret = new StringBuilder();
        WebElement table = getDriver().findElement(By.xpath("table[@border=1]"));
        writeOutput(table.toString());
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        rows.forEach((WebElement row)->{
            List<WebElement> cells = row.findElements(By.tagName("td"));
            cells.forEach((WebElement cell)->{
                ret.append(cell.getText()).append(", ");
            });
            ret.append('\n');
        });
        writeOutput(ret);
        return ret.toString();
    }

    @Override
    public void afterReadingQuery() {
        getDriver()
            .findElement(
                By.xpath("a[@href='AccountBalanceSumDescr.asp']")
            ).click();
    }
    
}
