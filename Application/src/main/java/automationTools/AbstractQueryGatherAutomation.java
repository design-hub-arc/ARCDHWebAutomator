package automationTools;

import csv.CsvFile;
import csv.CsvFileRequirements;
import csv.CsvRow;
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
    private final CsvFileRequirements fileReqs;
    private CsvFile queryFile;
    private final CsvFile resultFile;
    private final String inputUrl;
    private final String resultUrl;
    
    public AbstractQueryGatherAutomation(String autoName, String description, String inputUrl, CsvFileRequirements reqs, String resultUrl) {
        super(autoName, description);
        fileReqs = reqs;
        this.inputUrl = inputUrl;
        this.resultUrl = resultUrl;
        queryFile = null;
        resultFile = new CsvFile();
    }
    
    // methods inherited from QueryingAutomation
    /**
     * 
     * @return the URL where this should attempt to send queries to a webpage,
     * usually to form elements.
     */
    @Override
    public String getInputUrl(){
        return inputUrl;
    }
    
    /**
     * Used to specify what columns CSV data fed into
     * this must contain.
     * 
     * @return the requirements for CSV files fed into this
     */
    @Override
    public CsvFileRequirements getQueryFileReqs(){
        return fileReqs;
    }
    
    /**
     * 
     * @param file the file which will be used to feed queries to this automation
     */
    @Override
    public void setQueryFile(CsvFile file){
        queryFile = file;
    }
    
    /**
     * 
     * @return the CsvFile which is feeding queries into this automation.
     */
    @Override
    public CsvFile getQueryFile(){
        return queryFile;
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
        while(!hasNoMoreQueries()){
            driver.get(getInputUrl());
            ExpectedCondition<Boolean> e  = ExpectedConditions.urlMatches(getInputUrl());
            getWait().until(e);
            
            q = getNextQuery();
            inputQuery(q);
            
            e = ExpectedConditions.urlMatches(getResultUrl());
            try{
                getWait().until(e); 
                readQueryResult(result);
            } catch(TimeoutException timeOut){
                Logger.logError("AbstractQueryGatherAutomation.doRun", "Did not transition to result page after inputting query: [" + q + "]");
                Logger.logError("AbstractQueryGatherAutomation.doRun", timeOut);
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
