package automations;

import io.FileRequirements;

/**
 * The QueryingAutomation interface should
 * be implemented by automations that require
 * a user-submitted file to specify what 
 * queries to perform.
 * 
 * May move this to a helper class so it
 * isn't just an interface.
 * 
 * @author Matt Crow
 */
public interface QueryingAutomation {
    public QueryManager getQueryManager();
    
    /**
     * Move this to QueryManager
     * 
     * Used to verify if a file adheres to the
     * standards specified by the FileRequirements
     * passed to setQueryFileReqs.
     * 
     * @param fileText the text of the file to validate
     * @return whether or not the file is valid
     */
    public boolean validateFile(String fileText);
}
