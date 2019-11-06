package pages;

import io.QueryFileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import util.SafeString;

/**
 *
 * @author Matt
 */
public class TestPage extends AbstractPageTemplate{
    
    public TestPage(){
        super("https://www.google.com/", "https://www.google.com/search");
    }
    @Override
    public void inputQuery(SafeString query) {
        WebElement queryBox = this.getDriver().findElement(By.name("q"));
        System.out.println(queryBox);
        queryBox.sendKeys(query);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        queryBox.submit();
    }

    @Override
    public SafeString readQueryResult() {
        WebElement numResultBox = getDriver().findElement(By.id("resultStats"));
        System.out.println("num results is [" + numResultBox.getText() + "]");
        SafeString ret = new SafeString(numResultBox.getText().toCharArray());
        ret.append('\n');
        return ret;
    }
    
    public static void main(String[] args) throws IOException{
        TestPage p = new TestPage();
        char[] file = new QueryFileReader().readStream(TestPage.class.getResourceAsStream("/testFile.csv")).toCharArray();
        p.run(file);
    }

    @Override
    public void afterReadingQuery() {
        getDriver().get(getInputUrl());
    }
}
