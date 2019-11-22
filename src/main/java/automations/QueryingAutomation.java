package automations;

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
}
