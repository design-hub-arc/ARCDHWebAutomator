package automationTools;

import io.CsvFile;

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
     * @param file the CsvFile to feed to this' QueryManager
     */
    public default void setInputFile(CsvFile file){
        getQueryManager().setQueryFile(file);
    }
    
    public QueryManager getQueryManager();
}
