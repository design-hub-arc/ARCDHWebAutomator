package automations;

import io.FileSelector;
import io.ResultFileWriter;
import java.io.File;
import java.io.IOException;
import org.openqa.selenium.WebDriver;
import logging.Logger;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * This class serves as the base for automations
 * for the PeopleSoft financial website.
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
public abstract class AbstractPeopleSoftAutomation extends AbstractAutomation implements QueryingAutomation{
    private final QueryManager queryManager;
    private final String resultURL;
    private final StringBuilder result;
    
    /**
     * 
     * @param n the name to display for this automation.
     * @param desc the description of what this automation does
     * @param resultUrl the URL of the webpage where this should read the result of its query
     * @param q the QueryManager which manages all the input data used by this object
     */
    public AbstractPeopleSoftAutomation(String n, String desc, String resultUrl, QueryManager q){
        super(n, desc);
        resultURL = resultUrl;
        result = new StringBuilder();
        queryManager = q;
    }
    
    /**
     * The QueryManager formats and stores the
     * queries that the automation should input.
     * 
     * @return the QueryManager for this automation
     */
    @Override
    public final QueryManager getQueryManager(){
        return queryManager;
    }
    
    @Override
    public AbstractAutomation setLogger(Logger l){
        queryManager.setLogger(l);
        return super.setLogger(l);
    }
    
    public final String getResultUrl(){
        return resultURL;
    }
    
    /**
     * Dequeues the next query,
     * and passes it to the inputQuery method.
     * If there are no queries in the queue, throws a NoSuchElementException
     */
    private void doInputQuery(){
        inputQuery(queryManager.getNextQuery());
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
    
    public final void preRun(WebDriver drive, String fileText){
        result.delete(0, result.length());
        queryManager.setQueryFile(fileText);
        setDriver(drive);
        
        writeOutput("Running " + getClass().getName());
    }
    
    /**
     * Runs the automation,
     * then allows the user to save the results as a file on their computer.
     * 
     */
    @Override
    public void doRun(){
        WebDriver drive = getDriver();
        drive.get(queryManager.getInputUrl());
        String url = null;
        boolean queryInputted = false;
        while(isRunning()){
            ExpectedCondition e  = ExpectedConditions.urlMatches((queryInputted) ? resultURL : queryManager.getInputUrl());
            getWait().until(e); //this is compiling with uncheck method invocation, but the documentation doesn't help, and the application still works
            url = drive.getCurrentUrl();
            
            
            int idx = url.indexOf('?');
            if(idx != -1){
                url = url.substring(0, idx);
            }
            writeOutput("Current URL is " + url);
            
            if(url.equalsIgnoreCase(queryManager.getInputUrl()) && !queryInputted){
                queryInputted = true;
                doInputQuery();
            } else if(url.equalsIgnoreCase(resultURL) && queryInputted){
                queryInputted = false;
                doReadResult();
                if(queryManager.isEmpty()){
                    //need this in here, otherwise it exits after inputing the last query
                    writeOutput("Done with browser. Quitting.");
                    quit();
                }
            } else {
                reportError("Ahhh bad URL " + url);
                quit();
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
