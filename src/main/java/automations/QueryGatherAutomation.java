package automations;

import io.FileRequirements;
import logging.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * The QueryGatherAutomation follows a
 * basic process:<br>
 * 1. take a list of queries<br>
 * 2. for each query in the list, do the following:<br>
 *  a. input the query and submit it<br>
 *  b. read and store the result of the query<br>
 * 3. save the completed result to a file<br>
 * 
 * @author Matt Crow
 */
public abstract class QueryGatherAutomation extends AbstractAutomation implements QueryingAutomation, ReadingAutomation{
    private final QueryManager queryManager;
    private final ResultManager resultManager;
    
    public QueryGatherAutomation(String autoName, String description, QueryManager q, ResultManager r){
        super(autoName, description);
        queryManager = q;
        resultManager = r;
    }
    public QueryGatherAutomation(String autoName, String description, QueryManager q, String resultUrl){
        this(autoName, description, q, new ResultManager(resultUrl));
    }
    public QueryGatherAutomation(String autoName, String description, String inputUrl, FileRequirements reqs, ResultManager r){
        this(autoName, description, new QueryManager(inputUrl, reqs), r);
    }
    public QueryGatherAutomation(String autoName, String description, String inputUrl, FileRequirements reqs, String resultUrl) {
        this(autoName, description, new QueryManager(inputUrl, reqs), new ResultManager(resultUrl));
    }

    /**
     * Reformats the file containing queries which 
     * will be inputted by the automation 
     * @param fileText the un-reformatted file text
     */
    @Override
    public final void setInputFile(String fileText){
        queryManager.setQueryFile(fileText);
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
    
    /**
     * The ResultManager is used to keep track of
     * what this automation has recorded from webpages
     * it has visited.
     * 
     * @return the ResultManager for this automation
     */
    @Override
    public final ResultManager getResultManager(){
        return resultManager;
    }
    
    /**
     * Sets the object which should receive output
     * from this class, its query manager, and its 
     * result manager.
     * 
     * @param l the object with which this should log messages
     * @return this, for chaining purposes
     */
    @Override
    public AbstractAutomation setLogger(Logger l){
        queryManager.setLogger(l);
        resultManager.setLogger(l);
        return super.setLogger(l);
    }
    
    @Override
    public void doRun() {
        WebDriver driver = getDriver();
        resultManager.clear();
        driver.get(queryManager.getInputUrl()); //go to input page
        String url = "";
        while(isRunning()){
            url = driver.getCurrentUrl();
            int idx = url.indexOf('?');
            if(idx != -1){
                url = url.substring(0, idx);
            }
            writeOutput("Current URL is " + url);
            
            if(url.equalsIgnoreCase(queryManager.getInputUrl())){
                inputQuery(queryManager.getNextQuery());
            } else {
                reportError("Began run iteration while not on input URL. Make sure the readResultMethod returns to input URL.");
                quit();
            }
            
            ExpectedCondition e  = ExpectedConditions.urlMatches(resultManager.getResultUrl());
            getWait().until(e); //this is compiling with uncheck method invocation, but the documentation doesn't help, and the application still works
            
            resultManager.append(readQueryResult());
            afterReadingQuery();
            
            if(queryManager.isEmpty()){
                writeOutput("Done running, quitting browser.");
                quit();
            }
        }
        
        resultManager.saveToFile();
        writeOutput("Automation completed successfully");
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
