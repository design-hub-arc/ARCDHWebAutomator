package automations;

import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class TableTest extends AbstractAutomation{
    private static final String DESC = "";
    
    public TableTest() {
        super(
            "Table test", 
            DESC,
            "https://www.google.com/", 
            "https://developer.mozilla.org/en-US/docs/Web/HTML/Element/table"
        );
    }

    @Override
    public void inputQuery(String query) {
        getDriver().get(getResultUrl());
    }

    @Override
    public String readQueryResult() {
        ////*[@id="wikiArticle"]/table[1]
        HtmlTable table = new HtmlTable(getDriver().findElement(By.xpath("//*[@id=\"wikiArticle\"]/table[2]")));
        return table.toCsv();
    }

    @Override
    public void afterReadingQuery() {
        
    }

    @Override
    public String formatFile(String fileText) {
        return "this will only run once";
    }
}
