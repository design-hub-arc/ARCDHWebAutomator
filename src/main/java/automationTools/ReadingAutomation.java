package automationTools;

/**
 * ReadingAutomation should be implemented by automations
 * which read and store information from a webpage.
 * 
 * @see AbstractQueryGatherAutomation
 * 
 * @author Matt Crow
 */
public interface ReadingAutomation {
    public ResultManager getResultManager();
}
