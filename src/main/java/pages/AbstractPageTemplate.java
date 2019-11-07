package pages;

import io.FileSelector;
import io.ResultFileWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * @author Matt Crow
 */
public abstract class AbstractPageTemplate {
    private final String inputURL;
    private final String resultURL;
    private final LinkedList<String> queryFile;
    private final StringBuilder result;
    private WebDriver driver;
    private boolean done;
    private boolean showOutput;
    //private OutputStream out;
    private OutputStreamWriter out;
    
    /**
     * 
     * @param inputUrl the URL of the webpage where this should input queries
     * @param resultUrl the URL of the webpage where this should read the result of its query
     */
    public AbstractPageTemplate(String inputUrl, String resultUrl){
        inputURL = inputUrl;
        resultURL = resultUrl;
        queryFile = new LinkedList<>();
        result = new StringBuilder();
        done = true;
        driver = null;
        showOutput = true;
        out = new OutputStreamWriter(System.out);
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
    public final OutputStreamWriter getOutputStream(){
        return out;
    }
    public final void writeOutput(CharSequence output){
        System.out.println(output);
        //out.write(output.chars());
        /*
        
        byte[] bytes = new byte[output.length() * Character.BYTES];
        int len = output.length();
        int[] chars = output.chars().toArray();
        
        for(int i = 0; i < len; i++){
            //how do I convert int array to byte array?
        }
        out.write(bytes);*/
    }
    
    public String extractNextQuery(){
        String ret;
        
        if(queryFile.isEmpty()){
            throw new NoSuchElementException("Query file is empty.");
        }
        
        if(showOutput){
            writeOutput("Before Dequeing:");
            queryFile.forEach((query)->{
                writeOutput(query);
            });
        }
        
        ret = queryFile.removeFirst();
        
        if(showOutput){
            writeOutput("After Dequeing:");
            queryFile.forEach((query)->{
                writeOutput(query);
            });
        }
        
        return ret;
    }
    
    private void doInputQuery(){
        String nextQuery = extractNextQuery();
        if(showOutput){
            writeOutput("Inputting query:\n" + nextQuery);
        }
        
        inputQuery(nextQuery);
    } 
    private void doReadResult(){
        String queryResult = readQueryResult();
        if(showOutput){
            writeOutput("Reading query result:\n" + queryResult);
        }
        result.append(queryResult);
        afterReadingQuery();
    }
    
    public void run(String fileText, boolean displayOutput){
        showOutput = displayOutput;
        queryFile.clear();
        String[] split = fileText.split("\n");
        Arrays.stream(split).forEach((query)->{
            queryFile.add(query);
        });
        
        done = false;
        //change this
        System.setProperty("webdriver.chrome.driver", "C:/Users/Matt/Desktop/chromedriver.exe");
        driver = new ChromeDriver();
        
        
        writeOutput("Running " + getClass().getName());
        writeOutput("Query file is");
        queryFile.forEach((query)->{
            writeOutput(query);
        });
        
        driver.get(inputURL);
        String url;
        while(!done){
            url = driver.getCurrentUrl();
            int idx = url.indexOf('?');
            //out.println("idx is " + idx);
            if(idx != -1){
                url = url.substring(0, idx);
            }
            writeOutput("Current URL is " + url);
            
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
                new ResultFileWriter().writeToFile(f, result.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        
        driver.quit();
    }
    public void run(String s){
        run(s, true);
    }
    
    public abstract void inputQuery(String query);
    public abstract String readQueryResult();
    public abstract void afterReadingQuery();
}
