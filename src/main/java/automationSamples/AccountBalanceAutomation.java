package automationSamples;

import automationTools.AbstractPeopleSoftAutomation;
import io.CsvFile;
import io.CsvFileRequirements;
import io.CsvRow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class AccountBalanceAutomation extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Queries the PeopleSoft account balance summary page.";
    private static final String BU_HEADER = "Business Unit";
    private static final String ACCT_HEADER = "Account";
    private static final String FND_HEADER = "Fund";
    private static final String ORG_HEADER = "Org/DeptID";
    private static final String PROG_HEADER = "Program";
    private static final String SUBCLS_HEADER = "Sub-Class";
    private static final String PROJ_HEADER = "Project/Grant";
    
    private static final String[] HEADERS = new String[]{
        BU_HEADER,
        ACCT_HEADER,
        FND_HEADER,
        ORG_HEADER,
        PROG_HEADER,
        SUBCLS_HEADER,
        PROJ_HEADER
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
    public void inputQuery(CsvRow query) {
        HashMap<String, String> nameToHeader = new HashMap<>();
        nameToHeader.put("BusinessUnit", BU_HEADER);
        nameToHeader.put("Account", ACCT_HEADER);
        nameToHeader.put("Fund", FND_HEADER);
        nameToHeader.put("ORG", ORG_HEADER);
        nameToHeader.put("Program", PROG_HEADER);
        nameToHeader.put("SubClass", SUBCLS_HEADER);
        nameToHeader.put("ProjectGrant", PROJ_HEADER);
        
        nameToHeader.forEach((name, header)->{
            awaitFindElement(By.name(name)).sendKeys(query.get(header));
        });
        
        awaitFindElement(By.name("Query")).click();
    }

    @Override
    public ArrayList<CsvRow> readQueryResult() {
        HtmlTable table = new HtmlTable(awaitFindElement(By.xpath("//table[@border=1]")));
        CsvFile file = table.toCsvFile();
        
        if(getResultManager().getCsvFile().getHeaderCount() == 0){
            //don't have headers yet
            CsvFile result = getResultManager().getCsvFile();
            file.getHeaders().forEach((header)->result.addHeader(header));
        }
        
        return file.getBody();
    }
}
