package automationSamples;

import automationTools.ResultManager;
import automationTools.ReadingAutomation;
import automationTools.AbstractAutomation;
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
        ///html/body/table[3]
        getDriver().switchTo().frame("frame_More_Examples"); 
        //since the table is inside the IFrame, we need to switch to the frame before accessing the element
        HtmlTable table = new HtmlTable(awaitFindElement(By.xpath("//html/body/table[3]")));
        String text = table.toCsv(new String[]{"Capitals that don't exist"});
        r.append(text);
        r.saveToFile();
    }

    @Override
    public ResultManager getResultManager() {
        return r;
    }
}
