package automationTools;

import automationTools.QueryManager;

/**
 * The QueryingAutomation interface should
 * be implemented by automations that require
 * a user-submitted file to specify what 
 * queries to perform.
 * 
 * @author Matt Crow
 */
public interface QueryingAutomation {
    public void setInputFile(String fileText);
    public QueryManager getQueryManager();
}
