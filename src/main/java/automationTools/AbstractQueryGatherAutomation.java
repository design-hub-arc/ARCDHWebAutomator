package automationTools;

import csv.CsvFile;
import csv.CsvFileRequirements;
import csv.CsvRow;
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
    private final CsvFile resultFile;
    private final String resultUrl;
    
    public AbstractQueryGatherAutomation(String autoName, String description, String inputUrl, CsvFileRequirements reqs, String resultUrl) {
        super(autoName, description);
        queryManager = new QueryManager(this, inputUrl, reqs);
        this.resultUrl = resultUrl;
        resultFile = new CsvFile();
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
    public final String getResultUrl(){
        return resultUrl;
    }
    @Override
    public CsvFile getResultFile(){
        return resultFile;
    }
    
    @Override
    public void doRun() {
        WebDriver driver = getDriver();
        CsvFile result = getResultFile();
        result.clear();
        initResult();
        CsvRow q;
        while(!queryManager.isEmpty()){
            driver.get(queryManager.getInputUrl());
            ExpectedCondition<Boolean> e  = ExpectedConditions.urlMatches(queryManager.getInputUrl());
            getWait().until(e);
            
            q = queryManager.getNextQuery();
            inputQuery(q);
            
            e = ExpectedConditions.urlMatches(getResultUrl());
            try{
                getWait().until(e); 
                readQueryResult(result);
            } catch(TimeoutException timeOut){
                reportError("Did not transition to result page after inputting query: [" + q + "]");
                reportError(timeOut);
            }
        }
        saveResultToFile();
    }
    
    /**
     * Called prior to running the automation,
     * after clearing the result of previous runs.
     * 
     * Subclasses should override this method if
     * they need to perform any tasks prior to 
     * running the automation.
     */
    public void initResult(){
        
    }
    
    /**
     * Use the provided query to provide input for
     * elements on the page specified by this' input URL.
     * After filling out the page, click any 'submit' buttons.
     * 
     * @param query the next row from the CSV data source file 
     */
    public abstract void inputQuery(CsvRow query);
    
    /**
     * Parse the web page specified by this' result URL.
     * This method should then use data from the webpage to
     * change this' result file.
     * 
     * @param saveFile the result file for this automation
     */
    public abstract void readQueryResult(CsvFile saveFile);
}
