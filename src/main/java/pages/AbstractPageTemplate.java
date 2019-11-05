package pages;

import java.io.PrintStream;
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
    
    
    
    public SafeString extractNextQuery() throws Exception{
        int endOfQuery = queryFile.indexOf('\n');
        if(endOfQuery == -1){
            throw new Exception("No more queries to process");
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
        SafeString nextQuery = null;
        SafeString queryResult = null;
        while(!done){
            url = driver.getCurrentUrl();
            out.println("Current URL is " + url);
            
            if(url.equalsIgnoreCase(inputURL)){
                try {
                    //input next query
                    nextQuery = extractNextQuery();
                    out.println("Inputting query:");
                    nextQuery.print();
                    inputQuery(nextQuery);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    done = true;
                }
            } else if(url.equalsIgnoreCase(resultURL)){
                queryResult = readQueryResult();
                out.println("Reading query result:");
                queryResult.print();
                result.append(queryResult);
            } else {
                //
            }
            done = true;
            if(nextQuery != null){
                nextQuery.clearValue();
            }
            if(queryResult != null){
                queryResult.clearValue();
            }
        }
        clean();
        driver.quit();
    }
    
    public void clean(){
        queryFile.clearValue();
        result.clearValue();
    }
    
    public abstract void inputQuery(SafeString query);
    public abstract SafeString readQueryResult();
}
