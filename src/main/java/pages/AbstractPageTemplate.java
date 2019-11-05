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
    }
    
    
    
    public void clean(){
        result.clearValue();
    }
    
    public char[] extractNextQuery() throws Exception{
        int endOfQuery = queryFile.indexOf('\n');
        if(endOfQuery == -1){
            throw new Exception("No more queries to process");
        }
        SafeString ss = queryFile.substring(0, endOfQuery);
        System.out.println("Substring");
        ss.print();
        System.out.println("Before removing");
        queryFile.print();
        System.out.println("After");
        queryFile.removeFromStart(endOfQuery + 1);
        queryFile.print();
        
        return ss.toCharArray();
    }
    
    public void run(char[] a){
        queryFile.append(a);
        done = false;
        
        //change this
        System.setProperty("webdriver.chrome.driver", "C:/Users/Matt/Desktop/chromedriver.exe");
        
        WebDriver driver = new ChromeDriver();
        driver.get(inputURL);
        while(!done){
            String url = driver.getCurrentUrl();
            System.out.println(url);
            
            if(url.equalsIgnoreCase(inputURL)){
                try {
                    //input next query
                    inputQuery(extractNextQuery());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    done = true;
                }
            } else if(url.equalsIgnoreCase(resultURL)){
                result.append(readQueryResult());
            } else {
                //
            }
            done = true;
        }
        driver.quit();
    }
    
    public abstract void inputQuery(char[] query);
    public abstract char[] readQueryResult();
}
