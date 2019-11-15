package automations;

import io.CsvParser;
import io.QueryFileReader;
import java.io.IOException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Matt
 */
public class GoogleSearch extends AbstractAutomation{
    private static final String DESC = "Performs the Google searches contained in a file, then gives the number of results.";
    private static final String[] HEADERS = new String[]{
        "Query"
    };
    private static final String FILE_REQ = 
        "Input files must be in CSV format, "
        + "and should contain at least one column, "
        + "labeled 'Query'.";
    
    public GoogleSearch(){
        super(
            "Google Searches", 
            DESC, 
            "https://www.google.com/", 
            "https://www.google.com/search"
        );
        setFileReqDesc(FILE_REQ);
    }
    @Override
    public void inputQuery(String query) {
        WebElement queryBox = this.getDriver().findElement(By.name("q"));
        queryBox.sendKeys(query);
        queryBox.submit();
    }

    @Override
    public String readQueryResult() {
        WebElement numResultBox = getDriver().findElement(By.id("resultStats"));
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
