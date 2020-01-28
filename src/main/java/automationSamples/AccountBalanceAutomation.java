package automationSamples;

import automationTools.AbstractPeopleSoftAutomation;
import io.CsvFileRequirements;
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
    
    private static final CsvFileRequirements FILE_REQ = new CsvFileRequirements(
        "Input files must be in CSV format, "
        + "and should contain the following columns: "
        + Arrays.toString(HEADERS), HEADERS
    );
    
    public AccountBalanceAutomation(){
        super(
            "PSReports Account Balance",
            DESC,
            "https://psreports.losrios.edu/AccountBalanceSumDescr.asp", 
            FILE_REQ,
            "https://psreports.losrios.edu/AccountBalanceSumDescrQ.asp"
        );
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
        //todo: remove the headers if this is not the first result
        HtmlTable table = new HtmlTable(awaitFindElement(By.xpath("//table[@border=1]")));
        String text = table.toCsv();
        if(!getResultManager().getResult().equals("")){
            //already have headers, so remove them from the result
            int idx = text.indexOf('\n');
            if(idx == -1){
                //all headers. Get rid of it
                text = "";
            } else {
                text = text.substring(idx + 1);
            }
        }
        return text;
    }
}
