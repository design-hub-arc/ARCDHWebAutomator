package automationTools;

/**
 * The QueryingAutomation interface should
 * be implemented by automations that require
 * a user-submitted file to specify what 
 * queries to perform.
 * 
 * @see AbstractQueryGatherAutomation
 * 
 * @author Matt Crow
 */
public interface QueryingAutomation {
    /**
     * 
     * @param fileText the text to feed to this' QueryManager
     */
    public default void setInputFile(String fileText){
        getQueryManager().setQueryFile(fileText);
    }
    
    public QueryManager getQueryManager();
}
