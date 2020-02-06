package util;

import io.CsvFile;
import io.CsvFileException;
import io.CsvParser;
import io.CsvRow;
import java.util.ArrayList;
import java.util.HashMap;
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
     * Converts the text content of this HTML table's
     * th and td elements into a CSV file, <b>with commas removed from their text</b>
     * Only considers columns with the given headers. If any of the columns are not in this table, 
     * throws a CsvFileException.
     * 
     * @param columns
     * @return 
     */
    public String toCsv(String[] columns){
        StringBuilder ret = new StringBuilder();
        HashMap<String, Integer> cols = new HashMap<>();
        ArrayList<String> validCols = new ArrayList<>();
        int idx;
        for(String column : columns){
            idx = getColumnIdx(column);
            if(idx == -1){
                String tableHeaders = table
                    .findElement(By.tagName("tr"))
                    .findElements(By.xpath(".//th|.//td"))
                    .stream()
                    .map((WebElement e)->e.getText())
                    .collect(Collectors.joining(", "));
                throw new CsvFileException("Html table does not contain the column " + column + ". Instead, it has the columns [" + tableHeaders + "]");
            } else {
                cols.put(column, idx);
                validCols.add(column);
            }
        }
        int numCols = validCols.size();
        
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        
        rows.forEach((WebElement row)->{
            for(int i = 0; i < numCols; i++){
                List<WebElement> cells = row.findElements(By.xpath(".//th|.//td"));
                ret.append(cells.get(cols.get(validCols.get(i))).getText().replaceAll(",", ""));
                if(i != numCols - 1){
                    ret.append(",");
                }
            }
            ret.append(CsvParser.NEW_LINE);
        });
        return ret.toString();
    }
    
    /**
     * Converts the text content of this HTML table's
     * th and td elements into a CSV file, <b>with commas removed from their text</b>
     * @return 
     */
    public String toCsv(){
        boolean debug = false;
        StringBuilder ret = new StringBuilder();
        if(debug){
            System.out.println("Table is " + table.toString());
        }
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        if(debug){
            System.out.println(rows.size() + " rows");
        }
        rows.forEach((WebElement row)->{
            //                                                  in the row, th or td
            List<WebElement> cells = row.findElements(By.xpath(".//th|.//td"));
            if(debug){
                System.out.println(cells.size() + " cells");
            }
            for(int i = 0; i < cells.size(); i++){
                ret.append(cells.get(i).getText().replaceAll(",", ""));
                if(i != cells.size() - 1){
                    ret.append(", ");
                }
            }
            ret.append(CsvParser.NEW_LINE);
        });
        if(debug){
            System.out.println("Ret is \n" + ret);
        }
        return ret.toString();
    }
    
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
