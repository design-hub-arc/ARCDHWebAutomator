package automationTools;

import io.FileRequirements;
import logging.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * The AbstractQueryGatherAutomation follows a
 basic process:<br>
 * 1. take a list of queries<br>
 * 2. for each query in the list, do the following:<br>
 *  a. input the query and submit it<br>
 *  b. read and store the result of the query<br>
 * 3. save the completed result to a file<br>
 * 
 * @author Matt Crow
 */
public abstract class AbstractQueryGatherAutomation extends AbstractAutomation implements QueryingAutomation, ReadingAutomation{
    private final QueryManager queryManager;
    private final ResultManager resultManager;
    
    public AbstractQueryGatherAutomation(String autoName, String description, QueryManager q, ResultManager r){
        super(autoName, description);
        queryManager = q;
        resultManager = r;
    }
    public AbstractQueryGatherAutomation(String autoName, String description, QueryManager q, String resultUrl){
        this(autoName, description, q, new ResultManager(resultUrl));
    }
    public AbstractQueryGatherAutomation(String autoName, String description, String inputUrl, FileRequirements reqs, ResultManager r){
        this(autoName, description, new QueryManager(inputUrl, reqs), r);
    }
    public AbstractQueryGatherAutomation(String autoName, String description, String inputUrl, FileRequirements reqs, String resultUrl) {
        this(autoName, description, new QueryManager(inputUrl, reqs), new ResultManager(resultUrl));
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
        String q;
        while(isRunning()){
            driver.get(queryManager.getInputUrl());
            ExpectedCondition<Boolean> e  = ExpectedConditions.urlMatches(queryManager.getInputUrl());
            getWait().until(e);
            
            q = queryManager.getNextQuery();
            inputQuery(q);
            
            e = ExpectedConditions.urlMatches(resultManager.getResultUrl());
            try{
                getWait().until(e); 
                resultManager.append(readQueryResult());
            } catch(TimeoutException timeOut){
                reportError("Did not transition to result page after inputting query: [" + q + "]");
                reportError(timeOut);
            }
            
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
}
