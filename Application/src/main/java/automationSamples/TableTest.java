package automationSamples;

import automationTools.ReadingAutomation;
import automationTools.AbstractAutomation;
import csv.CsvFile;
import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class TableTest extends AbstractAutomation implements ReadingAutomation{
    private static final String DESC = "Tests the program's HTML table reading capabilities.";
    
    private final CsvFile result;
    
    public TableTest() {
        super("Table test", DESC);
        result = new CsvFile();
    }
    
    @Override
    public void doRun() {
        getDriver().get("https://developer.mozilla.org/en-US/docs/Web/HTML/Element/table");
        ///html/body/table[3]
        getDriver().switchTo().frame("frame_More_Examples"); 
        //since the table is inside the IFrame, we need to switch to the frame before accessing the element
        HtmlTable table = new HtmlTable(awaitFindElement(By.xpath("//html/body/table[3]")));
        CsvFile tableCsv = table.toCsvFile().getSubfile(new String[]{"Capitals"});
        result.addHeader("Capitals");
        tableCsv.getBody().forEach((row)->{
            result.addRow(row);
        });
        saveResultToFile();
        
        
        //new HTML table to csv:
        System.out.println(table.toCsvFile().toString());
    }
    
    @Override
    public String getResultUrl(){
        return "https://developer.mozilla.org/en-US/docs/Web/HTML/Element/table";
    }

    @Override
    public CsvFile getResultFile() {
        return result;
    }
}
