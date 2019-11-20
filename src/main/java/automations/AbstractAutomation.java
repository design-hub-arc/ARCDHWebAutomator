package automations;

import io.CsvFileException;
import io.FileSelector;
import io.ResultFileWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import static io.CsvParser.NEW_LINE;
import io.FileRequirements;
import java.util.List;
import logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
    private FileRequirements fileReq;
    private final String inputURL;
    private final String resultURL;
    private final LinkedList<String> queryFile;
    private final StringBuilder result;
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    private boolean done;
    private boolean showOutput;
    
    private final StringBuilder log; 
    private Logger logger;
    
    private static final Logger DEFAULT_LOGGER = new Logger(){
        @Override
        public void log(String s) {
            System.out.println(s);
        }

        @Override
        public String getLog() {
            return "";
        }
    };
    
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
        fileReq = FileRequirements.NO_REQ;
        inputURL = inputUrl;
        resultURL = resultUrl;
        queryFile = new LinkedList<>();
        result = new StringBuilder();
        done = true;
        driver = null;
        wait = null;
        showOutput = true;
        log = new StringBuilder();
        logger = DEFAULT_LOGGER;
    }
    
    public final String getName(){
        return name;
    }
    
    public final String getDescription(){
        return description;
    }
    
    public final void setFileReq(FileRequirements req){
        fileReq = req;
    }
    
    public final FileRequirements getFileReq(){
        return fileReq;
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
    
    /**
     * Waits for an element in the webpage to load,
     * then returns it.
     * 
     * Since FireFoxDriver appears to not block while the
     * webpage is loading, using driver.findElement(By by)
     * will usually through a StaleElementException,
     * so this method circumvents this problem.
     * 
     * @param by the locator used to find the element
     * @return the WebElement found by the "by" parameter.
     */
    public final WebElement awaitFindElement(By by){
        if(wait == null || driver == null){
            throw new NullPointerException("process is not running, so the WebDriver isn't set");
        }
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }
    
    /**
     * Waits for elements in the webpage to load,
     * then returns them.
     * 
     * Since FireFoxDriver appears to not block while the
     * webpage is loading, using driver.findElement(By by)
     * will usually through a StaleElementException,
     * so this method circumvents this problem.
     * 
     * @param by the locator used to find the elements
     * @return the WebElements found by the "by" parameter.
     */
    public final List<WebElement> awaitFindElements(By by){
        if(wait == null || driver == null){
            throw new NullPointerException("process is not running, so the WebDriver isn't set");
        }
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }
    
    /**
     * Sets the object which should receive output from the automation.
     * This defaults to sending output to System.out, but RunWindow calls this method,
     * passing in its ScrollableTextDisplay.
     * @param l an object implementing the logging.Logger interface
     */
    public final void setLogger(Logger l){
        logger = l;
    }
    
    public final boolean validateFile(String fileText) throws CsvFileException{
        formatFile(fileText);
        return true;
    }
    
    /**
     * Sends a string to the current logger,
     * <b>if showOutput is set to true</b>.
     * @param output the text to write to output, with a newline appended to the end.
     */
    public final void writeOutput(String output){
        if(!showOutput){
            return;
        }
        logger.log(output + "\n");
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
     * @param drive the WebDriver to run this automation on.
     * @param fileText the text of the data source file for this automation.
     * @param displayOutput whether or not to send output to this' current output stream
     */
    public void run(WebDriver drive, String fileText, boolean displayOutput){
        showOutput = displayOutput;
        queryFile.clear();
        result.delete(0, result.length());
        fileText = formatFile(fileText).trim();
        String[] split = fileText.split(NEW_LINE); //since fileText has all its line endings replace with NEW_LINE 
        Arrays.stream(split).forEach((query)->{
            queryFile.add(query);
        });
        
        done = false;
        driver = drive;
        wait = new WebDriverWait(drive, 10);
        
        writeOutput("Running " + getClass().getName());
        writeOutput("Query file is");
        queryFile.forEach((query)->{
            writeOutput(query);
        });
        writeOutput(String.format("(%d queries)", split.length));
        
        driver.get(inputURL);
        String url = null;
        boolean queryInputted = false;
        while(!done){
            ExpectedCondition e  = ExpectedConditions.urlMatches((queryInputted) ? resultURL : inputURL);
            wait.until(e); //this is compiling with uncheck method invocation, but the documentation doesn't help, and the application still works
            url = driver.getCurrentUrl();
            
            
            int idx = url.indexOf('?');
            if(idx != -1){
                url = url.substring(0, idx);
            }
            writeOutput("Current URL is " + url);
            
            if(url.equalsIgnoreCase(inputURL) && !queryInputted){
                queryInputted = true;
                doInputQuery();
            } else if(url.equalsIgnoreCase(resultURL) && queryInputted){
                queryInputted = false;
                doReadResult();
                if(queryFile.isEmpty()){
                    //need this in here, otherwise it exits after inputing the last query
                    done = true;
                    writeOutput("Done with browser. Quitting.");
                    driver.quit();
                }
            } else {
                System.err.println("Ahhh bad URL " + url);
                done = true;
                driver.quit();
                driver = null;
                wait = null;
            }
        }
        
        writeOutput("Saving file");
        FileSelector.createNewFile((File f)->{
            writeOutput("Attempting to write to " + f.getAbsolutePath());
            try {
                new ResultFileWriter().writeToFile(f, result.toString());
                writeOutput("file successfully written");
            } catch (IOException ex) {
                ex.printStackTrace();
                writeOutput("failed to write to file");
            }
        });
        
        
        writeOutput("process complete");
    }
    public void run(WebDriver driver, String s){
        run(driver, s, true);
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
