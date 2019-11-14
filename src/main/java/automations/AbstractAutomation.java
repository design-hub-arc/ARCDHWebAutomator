package automations;

import io.FileSelector;
import io.ResultFileWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import static io.CsvParser.NEW_LINE;

/**
 * An Automation is used to run a process in
 * the web browser.
 * The automations follow this sequence:
 * 1. take a data source file, usually providing values to fill into fields in a web page, and use it as a list of queries.
 * 2. Go to a designated start page.
 * 3. repeat steps 4-6 until the list of queries is empty:
 * 4. if the browser is on the given input URL, dequeue the next query, and fill out a form on the webpage, then submit the form.
 * 5. if the browser is on the given result URL, read the result, and append it to the accumulated results of all previous queries.
 * 6. return to the input URL.
 * 7. Write the combined results of every query to a file on the user's computer.
 * @author Matt Crow
 */
public abstract class AbstractAutomation {
    private final String name;
    private final String description;
    private String fileReqDesc;
    private final String inputURL;
    private final String resultURL;
    private final LinkedList<String> queryFile;
    private final StringBuilder result;
    private WebDriver driver;
    private boolean done;
    private boolean showOutput;
    private OutputStream out;
    
    /**
     * 
     * @param n the name to display for this automation.
     * @param desc the description of what this automation does
     * @param inputUrl the URL of the webpage where this should input queries
     * @param resultUrl the URL of the webpage where this should read the result of its query
     */
    public AbstractAutomation(String n, String desc, String inputUrl, String resultUrl){
        name = n;
        description = desc;
        fileReqDesc = "This automation accepts any file.";
        inputURL = inputUrl;
        resultURL = resultUrl;
        queryFile = new LinkedList<>();
        result = new StringBuilder();
        done = true;
        driver = null;
        showOutput = true;
        out = System.out;
    }
    
    public final String getName(){
        return name;
    }
    
    public final String getDescription(){
        return description;
    }
    
    public final void setFileReqDesc(String text){
        fileReqDesc = text;
    }
    
    public final String getFileReqDesc(){
        return fileReqDesc;
    }
    
    /**
     * 
     * @return the URL this should fill out forms on.
     */
    public final String getInputUrl(){
        return inputURL;
    }
    public final String getResultUrl(){
        return resultURL;
    }
    public final WebDriver getDriver(){
        return driver;
    }
    public final OutputStream getOutputStream(){
        return out;
    }
    
    /**
     * Sends a string to the current output stream,
     * <b>if showOutput is set to true</b>.
     * By default, this writes to System.out, but
     * later versions will be able to write to a file.
     * @param output the text to write to output, with a newline appended to the end.
     */
    public final void writeOutput(String output){
        if(!showOutput){
            return;
        }
        try {
            out.write((output + NEW_LINE).getBytes());
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Dequeues the next query,
     * and passes it to the inputQuery method.
     * If there are no queries in the queue, throws a NoSuchElementException
     */
    private void doInputQuery(){
        if(queryFile.isEmpty()){
            throw new NoSuchElementException("Query file is empty.");
        }
        
        writeOutput("Before Dequeing:");
        queryFile.forEach((query)->{
            writeOutput(query);
        });
        
        String nextQuery = queryFile.removeFirst();
        
        writeOutput("After Dequeing:");
        queryFile.forEach((query)->{
            writeOutput(query);
        });
        writeOutput("Inputting query:\n" + nextQuery);
        
        inputQuery(nextQuery);
    }
    
    /**
     * Calls the readQueryResult method,
     * then appends it to the end of the result text.
     * Afterwards, afterReadingQuery is called.
     */
    private void doReadResult(){
        String queryResult = readQueryResult();
        writeOutput("Reading query result:\n" + queryResult);
        result.append(queryResult);
        afterReadingQuery();
    }
    
    /**
     * Runs the automation,
     * then allows the user to save the results as a file on their computer.
     * 
     * @param fileText the text of the data source file for this automation.
     * @param displayOutput whether or not to send output to this' current output stream.
     * 
     * TODO: error handling, different WebDrivers
     */
    public void run(String fileText, boolean displayOutput){
        showOutput = displayOutput;
        queryFile.clear();
        result.delete(0, result.length());
        fileText = formatFile(fileText);
        String[] split = fileText.split(NEW_LINE); //since fileText has all its line endings replace with NEW_LINE 
        Arrays.stream(split).forEach((query)->{
            queryFile.add(query);
        });
        
        done = false;
        driver = new ChromeDriver();
        
        
        writeOutput("Running " + getClass().getName());
        writeOutput("Query file is");
        queryFile.forEach((query)->{
            writeOutput(query);
        });
        writeOutput(String.format("(%d queries)", split.length));
        
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
    
    /**
     * Converts the given text into the format used by this automation,
     * if the text cannot be converted, make sure you throw an exception!
     * @param fileText the text resulting from reading the file the user chose to read queries from
     * @return the newly formatted text.
     */
    public abstract String formatFile(String fileText);
    
    /**
     * Use the provided query to provide input for
     * elements on the page specified by this' input URL.
     * After filling out the page, click any 'submit' buttons.
     * 
     * @param query the next line of text from the data source file 
     */
    public abstract void inputQuery(String query);
    
    /**
     * Parse the web page specified by this' result URL.
     * Return the parsed content as a string 
     * so that it can be appended to the result file.
     * 
     * @return a string containing the relevant data in the web page.
     */
    public abstract String readQueryResult();
    
    /**
     * Perform any process required to return to the input URL.
     */
    public abstract void afterReadingQuery();
}
