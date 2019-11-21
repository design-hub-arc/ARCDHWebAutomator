package automations;

import io.CsvParser;
import io.FileRequirements;
import io.FileType;
import java.util.Arrays;
import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class AccountBalanceAutomation extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Queries the PeopleSoft account balance summary page.";
    private static final String[] HEADERS = new String[]{
        "Business Unit",
        "Account",
        "Fund",
        "Org/DeptID",
        "Program",
        "Sub-Class",
        "Project/Grant"
    };
    
    private static final FileRequirements FILE_REQ = new FileRequirements(
        "Input files must be in CSV format, "
        + "and should contain the following columns: "
        + Arrays.toString(HEADERS), FileType.CSV
    );
    
    public AccountBalanceAutomation(){
        super(
            "PSReports Account Balance",
            DESC,
            "https://psreports.losrios.edu/AccountBalanceSumDescr.asp",
            "https://psreports.losrios.edu/AccountBalanceSumDescrQ.asp"
        );
        setFileReq(FILE_REQ);
    }
    @Override
    public void inputQuery(String query) {
        String[] params = query.split(",");
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
            awaitFindElement(By.name(names[i])).sendKeys(params[i]);
        }
        
        //do I want autoclick to be optional?
        awaitFindElement(By.name("Query")).click();
    }

    @Override
    public String readQueryResult() {
        HtmlTable table = new HtmlTable(awaitFindElement(By.xpath("//table[@border=1]")));
        return table.toCsv();
    }

    @Override
    public void afterReadingQuery() {
        awaitFindElement(
            By.xpath("//a[@href='AccountBalanceSumDescr.asp']")
        ).click();
    }

    @Override
    public String formatFile(String fileText) {
        return new CsvParser(HEADERS).reformat(fileText, false);
    }
    
}
