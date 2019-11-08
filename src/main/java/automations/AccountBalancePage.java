package automations;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class AccountBalancePage extends AbstractAutomation{
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
        HtmlTable table = new HtmlTable(getDriver().findElement(By.xpath("//table[@border=1]")));
        return table.toCsv();
    }

    @Override
    public void afterReadingQuery() {
        getDriver()
            .findElement(
                By.xpath("//a[@href='AccountBalanceSumDescr.asp']")
            ).click();
    }
    
}
