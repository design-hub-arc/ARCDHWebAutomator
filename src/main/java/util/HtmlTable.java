package util;

import io.CsvFile;
import io.CsvRow;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * @author Matt
 */
public class HtmlTable {
    private final WebElement table;
    
    public HtmlTable(WebElement t){
        table = t;
    }
    
    /**
     * Returns the index of the column in this table
     * with the given column header. If the given column
     * does not exist, returns -1
     * 
     * @param colName
     * @return 
     */
    public int getColumnIdx(String colName){
        int ret = -1;
        List<WebElement> columns = table.findElements(By.tagName("th"));
        for(int i = 0; i < columns.size() && ret == -1; i++){
            if(columns.get(i).getText().equals(colName)){
                ret = i;
            }
        }
        return ret;
    }
    
    /**
     * Converts this HTML table into a CsvFile object.
     * 
     * @return 
     */
    public CsvFile toCsvFile(){
        boolean debug = true;
        
        CsvFile ret = new CsvFile();
        
        //first, gather headers
        List<WebElement> headers = table.findElements(By.tagName("th"));
        if(debug){
            System.out.println("Headers are " + headers.stream().map((header)->header.getText()).collect(Collectors.joining(", ")));
        }
        headers.forEach((header)->ret.addHeader(header.getText()));
        
        //now, gather the body
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        rows.forEach((WebElement row)->{
            //previous versions needed By.xpath(".//td|.//th"), 
            //but that was probably just for the messed up table formatting on the PeopleSoft website
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if(debug){
                System.out.println("Row is " + cells.stream().map((cell)->cell.getText()).collect(Collectors.joining(", ")));
            }
            if(!cells.isEmpty()){
                CsvRow csvRow = new CsvRow(ret, cells.stream().map((cell)->cell.getText()).toArray(String[]::new));
                ret.addRow(csvRow);
            }
        });
        
        return ret;
    }
}
