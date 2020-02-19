package automationTools;

import csv.CsvFile;
import csv.CsvFileRequirements;
import csv.CsvRow;
import java.util.NoSuchElementException;

/**
 * The QueryingAutomation interface should
 * be implemented by automations that require
 * a user-submitted CsvFile to specify what 
 * queries to perform.
 * 
 * @see AbstractQueryGatherAutomation
 * 
 * @author Matt Crow
 */
public interface QueryingAutomation {
    
    /**
     * 
     * @return the URL where this should attempt to send queries to a webpage,
     * usually to form elements.
     */
    public abstract String getInputUrl();
    
    /**
     * Used to specify what columns CSV data fed into
     * this must contain.
     * 
     * @return the requirements for CSV files fed into this
     */
    public abstract CsvFileRequirements getQueryFileReqs();
    
    /**
     * 
     * @param queryFile the file which will be used to feed queries to this automation
     */
    public abstract void setQueryFile(CsvFile queryFile);
    
    /**
     * 
     * @return the CsvFile which is feeding queries into this automation.
     */
    public abstract CsvFile getQueryFile();
    
    
    public default boolean hasNoMoreQueries(){
        return getQueryFile().getRowCount() == 0;
    }
    
    public default CsvRow getNextQuery(){
        if(hasNoMoreQueries()){
            throw new NoSuchElementException("Query file is empty");
        }
        
        AbstractAutomation auto = (this instanceof AbstractAutomation) ? (AbstractAutomation)this : null;
        CsvFile queryFile = getQueryFile();
        
        if(auto != null){
            auto.writeOutput("Before dequeueing:\n" + queryFile.toString());
        }
        
        CsvRow nextQuery = queryFile.dequeueFirstRow();
        
        if(auto != null){
            auto.writeOutput("Dequeued: " + nextQuery.toString());
            auto.writeOutput("After dequeueing:\n" + queryFile.toString());
        }
        
        return nextQuery;
    }
}
