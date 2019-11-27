package util;

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
