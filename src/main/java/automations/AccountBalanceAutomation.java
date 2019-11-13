package automations;

import io.CsvParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class AccountBalanceAutomation extends AbstractAutomation{
    private static final String[] HEADERS = new String[]{
        "Business Unit",
        "Account",
        "Fund",
        "Org/DeptID",
        "Program",
        "Sub-Class",
        "Project/Grant"
    };
    
    public AccountBalanceAutomation(){
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

    @Override
    public String formatFile(String fileText) {
        return new CsvParser(HEADERS).reformat(fileText, true);
    }
    
}