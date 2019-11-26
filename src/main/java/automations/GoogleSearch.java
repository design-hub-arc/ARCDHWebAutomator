package automations;

import io.CsvFileRequirements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Matt
 */
public class GoogleSearch extends QueryGatherAutomation{
    private static final String DESC = "Performs the Google searches contained in a file, then gives the number of results.";
    private static final String[] HEADERS = new String[]{
        "Query"
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
    public void inputQuery(String query) {
        WebElement queryBox = awaitFindElement(By.name("q"));
        queryBox.sendKeys(query);
        queryBox.submit();
    }

    @Override
    public String readQueryResult() {
        WebElement numResultBox = awaitFindElement(By.id("resultStats"));
        String ret = numResultBox.getText() + '\n';
        return ret;
    }
    @Override
    public void afterReadingQuery() {
        getDriver().get(getQueryManager().getInputUrl());
    }
}
