package test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


/**
 *
 * @author matt
 */
public class Test {
    public static void main(String[] args){
        System.setProperty("webdriver.chrome.driver", "/Users/matt/Desktop/chromedriver");
        WebDriver drive = new ChromeDriver();
        drive.get("http://www.google.com");
        System.out.print(drive.getTitle());
    }
}
