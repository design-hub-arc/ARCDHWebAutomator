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
    private static final String[] HEADERS = new String[]{
        "Query"
    };
    public GoogleSearch(){
        super("https://www.google.com/", "https://www.google.com/search");
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
    
    public static void main(String[] args) throws IOException{
        GoogleSearch p = new GoogleSearch();
        String file = new QueryFileReader().readStream(GoogleSearch.class.getResourceAsStream("/googleSearches.txt"));
        p.run(file, true);
    } 

    @Override
    public String formatFile(String fileText) {
        return new CsvParser(HEADERS).reformat(fileText, false);
    }
}
