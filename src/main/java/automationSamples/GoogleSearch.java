package automationSamples;

import automationTools.AbstractQueryGatherAutomation;
import io.CsvFileRequirements;
import io.CsvRow;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Matt
 */
public class GoogleSearch extends AbstractQueryGatherAutomation{
    private static final String DESC = "Performs the Google searches contained in a file, then gives the number of results.";
    private static final String Q_HEADER = "Query";
    private static final String[] HEADERS = new String[]{
        Q_HEADER
    };
    private static final CsvFileRequirements FILE_REQ = new CsvFileRequirements(
        "Input files must be in CSV format, "
        + "and should contain at least one column, "
        + "labeled 'Query'.", HEADERS
    );
    
    public GoogleSearch(){
        super(
            "Google Searches", 
            DESC, 
            "https://www.google.com/",
            FILE_REQ,
            "https://www.google.com/search"
        );
    }
    @Override
    public void inputQuery(CsvRow query) {
        WebElement queryBox = awaitFindElement(By.name("q"));
        queryBox.sendKeys(query.get(Q_HEADER));
        queryBox.submit();
    }

    @Override
    public String readQueryResult() {
        WebElement numResultBox = awaitFindElement(By.id("resultStats"));
        String ret = numResultBox.getText() + '\n';
        
        return ret;
    }
}
