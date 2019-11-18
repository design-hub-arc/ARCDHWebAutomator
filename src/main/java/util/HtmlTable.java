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
    
    public String toCsv(){
        StringBuilder ret = new StringBuilder();
        System.out.println(table.toString());
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        rows.forEach((WebElement row)->{
            //                                                  in the row, th or td
            List<WebElement> cells = row.findElements(By.xpath("/th|/td"));
            cells.forEach((WebElement cell)->{
                ret.append(cell.getText()).append(", ");
            });
            ret.append('\n');
        });
        System.out.println(ret);
        return ret.toString();
    }
}
