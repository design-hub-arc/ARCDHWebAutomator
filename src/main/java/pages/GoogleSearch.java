package pages;

import io.QueryFileReader;
import java.io.IOException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import util.SafeString;

/**
 *
 * @author Matt
 */
public class GoogleSearch extends AbstractPageTemplate{
    
    public GoogleSearch(){
        super("https://www.google.com/", "https://www.google.com/search");
    }
    @Override
    public void inputQuery(SafeString query) {
        WebElement queryBox = this.getDriver().findElement(By.name("q"));
        queryBox.sendKeys(query);
        queryBox.submit();
    }

    @Override
    public SafeString readQueryResult() {
        WebElement numResultBox = getDriver().findElement(By.id("resultStats"));
        SafeString ret = new SafeString(numResultBox.getText().toCharArray());
        ret.append('\n');
        return ret;
    }
    @Override
    public void afterReadingQuery() {
        getDriver().get(getInputUrl());
    }
    
    public static void main(String[] args) throws IOException{
        GoogleSearch p = new GoogleSearch();
        char[] file = new QueryFileReader().readStream(GoogleSearch.class.getResourceAsStream("/googleSearches.txt")).toCharArray();
        p.run(file, true);
    } 
}
