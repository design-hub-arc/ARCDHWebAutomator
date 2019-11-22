package automations;

import logging.Logger;
import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class TableTest extends AbstractAutomation implements ReadingAutomation{
    private static final String DESC = "Tests the program's HTML table reading capabilities.";
    
    private final ResultManager r;
    
    public TableTest() {
        super("Table test", DESC);
        r = new ResultManager("https://developer.mozilla.org/en-US/docs/Web/HTML/Element/table");
    }

    @Override
    public AbstractAutomation setLogger(Logger l){
        r.setLogger(l);
        return super.setLogger(l);
    }
    
    @Override
    public void doRun() {
        getDriver().get("https://developer.mozilla.org/en-US/docs/Web/HTML/Element/table");
        HtmlTable table = new HtmlTable(awaitFindElement(By.xpath("//*[@id=\"wikiArticle\"]/dl[2]/dd/table")));
        r.append(table.toCsv());
        r.saveToFile();
    }

    @Override
    public ResultManager getResultManager() {
        return r;
    }
}
