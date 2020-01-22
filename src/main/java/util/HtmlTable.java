package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    
    public String toCsv(String[] columns){
        StringBuilder ret = new StringBuilder();
        HashMap<String, Integer> cols = new HashMap<>();
        ArrayList<String> validCols = new ArrayList<>();
        int idx;
        for(String column : columns){
            idx = getColumnIdx(column);
            if(idx != -1){
                cols.put(column, idx);
                validCols.add(column);
            }
        }
        int numCols = validCols.size();
        
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        
        rows.forEach((WebElement row)->{
            WebElement cell;
            for(int i = 0; i < numCols; i++){
                cell = row.findElement(By.xpath(".//td[" + cols.get(validCols.get(i)) + "]"));
                ret.append(cell.getText().replaceAll(",", ""));
                if(i != numCols - 1){
                    ret.append(",");
                }
            }
            ret.append('\n');
        });
        return ret.toString();
    }
    
    /**
     * Converts the text content of this HTML table's
     * th and td elements into a CSV file, <b>with commas removed from their text</b>
     * @return 
     */
    public String toCsv(){
        StringBuilder ret = new StringBuilder();
        System.out.println("Table is " + table.toString());
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        System.out.println(rows.size() + " rows");
        rows.forEach((WebElement row)->{
            //                                                  in the row, th or td
            List<WebElement> cells = row.findElements(By.xpath(".//th|.//td"));
            System.out.println(cells.size() + " cells");
            for(int i = 0; i < cells.size(); i++){
                ret.append(cells.get(i).getText().replaceAll(",", ""));
                if(i != cells.size() - 1){
                    ret.append(", ");
                }
            }
            ret.append('\n');
        });
        System.out.println("Ret is \n" + ret);
        return ret.toString();
    }
}
