package automations;

import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class TableTest extends AbstractAutomation{
    private static final String DESC = "Tests the program's HTML table reading capabilities.";
    
    public TableTest() {
        super("Table test", DESC);
    }

    @Override
    public void doRun() {
        getDriver().get("https://developer.mozilla.org/en-US/docs/Web/HTML/Element/table");
        HtmlTable table = new HtmlTable(awaitFindElement(By.xpath("//*[@id=\"wikiArticle\"]/dl[2]/dd/table")));
        writeOutput(table.toCsv());
        //next, save the file
    }
}
