package automations;

import io.CsvParser;
import io.FileRequirements;
import io.FileType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Matt
 */
public class GoogleSearch extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Performs the Google searches contained in a file, then gives the number of results.";
    private static final String[] HEADERS = new String[]{
        "Query"
    };
    private static final FileRequirements FILE_REQ = new FileRequirements(
        "Input files must be in CSV format, "
        + "and should contain at least one column, "
        + "labeled 'Query'.", FileType.CSV
    );
    
    public GoogleSearch(){
        super(
            "Google Searches", 
            DESC, 
            "https://www.google.com/", 
            "https://www.google.com/search"
        );
        setQueryFileReqs(FILE_REQ);
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
        getDriver().get(getInputUrl());
    }

    @Override
    public String formatFile(String fileText) {
        return new CsvParser(HEADERS).reformat(fileText, false);
    }
}
