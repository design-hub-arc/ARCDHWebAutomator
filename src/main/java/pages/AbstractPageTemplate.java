package pages;

import io.FileSelector;
import io.ResultFileWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import util.SafeString;

/**
 * @author Matt Crow
 */
public abstract class AbstractPageTemplate {
    private final String inputURL;
    private final String resultURL;
    private final SafeString queryFile;
    private final SafeString result;
    private WebDriver driver;
    private boolean done;
    
    /**
     * 
     * @param inputUrl the URL of the webpage where this should input queries
     * @param resultUrl the URL of the webpage where this should read the result of its query
     */
    public AbstractPageTemplate(String inputUrl, String resultUrl){
        /*
        if(!inputUrl.startsWith("/")){
            inputUrl = "/" + inputUrl;
        }
        if(!resultUrl.startsWith("/")){
            resultUrl = "/" + resultUrl;
        }*/
        inputURL = inputUrl;
        resultURL = resultUrl;
        queryFile = new SafeString();
        result = new SafeString();
        done = true;
        driver = null;
    }
    
    public final String getInputUrl(){
        return inputURL;
    }
    public final String getResultUrl(){
        return resultURL;
    }
    public final WebDriver getDriver(){
        return driver;
    }
    
    
    
    public SafeString extractNextQuery(){
        int endOfQuery = queryFile.indexOf('\n');
        if(endOfQuery == -1){
            endOfQuery = queryFile.length(); //go to the end of the file
        }
        SafeString ss = queryFile.substring(0, endOfQuery);
        //System.out.println("Substring");
        //ss.print();
        //System.out.println("Before removing");
        //queryFile.print();
        //System.out.println("After");
        queryFile.removeFromStart(endOfQuery + 1);
        //queryFile.print();
        
        return ss;
    }
    
    private void doInputQuery(){
        SafeString nextQuery = extractNextQuery();
        System.out.println("Inputting query:");
        nextQuery.print();
        inputQuery(nextQuery);
        nextQuery.clearValue();
    } 
    private void doReadResult(){
        SafeString queryResult = readQueryResult();
        System.out.println("Reading query result:");
        queryResult.print();
        result.append(queryResult);
        afterReadingQuery();
        queryResult.clearValue();
    }
    
    public void run(char[] a){
        //change this to where I can write output to a file
        PrintStream out = System.out;
        queryFile.clearValue();
        queryFile.append(a);
        done = false;
        //change this
        System.setProperty("webdriver.chrome.driver", "C:/Users/Matt/Desktop/chromedriver.exe");
        driver = new ChromeDriver();
        
        
        out.println("Running " + getClass().getName());
        out.println("Query file is");
        queryFile.print();
        
        driver.get(inputURL);
        String url;
        while(!done){
            url = driver.getCurrentUrl();
            int idx = url.indexOf('?');
            //out.println("idx is " + idx);
            if(idx != -1){
                url = url.substring(0, idx);
            }
            out.println("Current URL is " + url);
            
            if(url.equalsIgnoreCase(inputURL)){
                doInputQuery();
            } else if(url.equalsIgnoreCase(resultURL)){
                doReadResult();
                if(queryFile.isEmpty()){
                    done = true;
                }
            } else {
                System.err.println("Ahhh bad URL " + url);
                done = true;
            }
        }
        
        FileSelector.createNewFile((File f)->{
            try {
                new ResultFileWriter().writeToFile(f, result);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        
        clean();
        driver.quit();
    }
    
    public void clean(){
        queryFile.clearValue();
        result.clearValue();
    }
    
    public abstract void inputQuery(SafeString query);
    public abstract SafeString readQueryResult();
    public abstract void afterReadingQuery();
}
