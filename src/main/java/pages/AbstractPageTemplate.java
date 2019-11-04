package pages;

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
    private boolean done;
    
    public AbstractPageTemplate(String inputUrl, String resultUrl){
        if(!inputUrl.startsWith("/")){
            inputUrl = "/" + inputUrl;
        }
        if(!resultUrl.startsWith("/")){
            resultUrl = "/" + resultUrl;
        }
        inputURL = inputUrl;
        resultURL = resultUrl;
        queryFile = new SafeString();
        result = new SafeString();
        done = true;
    }
    
    
    
    public void clean(){
        result.clearValue();
    }
    
    public char[] extractNextQuery(){
        
        return null;
    }
    
    public void run(char[] queryFile){
        done = false;
        
        //change this
        System.setProperty("webdriver.chrome.driver", "/Users/matt/Desktop/chromedriver");
        
        WebDriver driver = new ChromeDriver();
        while(!done){
            String url = driver.getCurrentUrl();
            //how to get pathname?
            
            //how to ignore case?
            if(url.contains(inputURL)){
                //input next query
            } else if(url.contains(resultURL)){
                result.append(readQueryResult());
            } else {
                //
            }
        }
    }
    
    public abstract void inputQuery(char[] query);
    public abstract char[] readQueryResult();
}
