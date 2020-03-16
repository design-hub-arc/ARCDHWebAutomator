package automationSamples;

import automationTools.AbstractQueryGatherAutomation;
import csv.CsvFile;
import csv.CsvFileRequirements;
import csv.CsvRow;
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
    private static final String RESULT_HEADER = "Result count";
    
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
    public void initResult(){
        getResultFile().addHeader(RESULT_HEADER);
    }
    
    @Override
    public void inputQuery(CsvRow query) {
        WebElement queryBox = awaitFindElement(By.name("q"));
        queryBox.sendKeys(query.get(Q_HEADER));
        queryBox.submit();
    }

    @Override
    public void readQueryResult(CsvFile saveFile) {
        WebElement numResultBox = awaitFindElement(By.id("result-stats"));
        CsvRow r = new CsvRow(saveFile);
        r.set(RESULT_HEADER, numResultBox.getText());
        saveFile.addRow(r);
    }
}
