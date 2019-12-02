package automationTools;

/**
 * This class currently serves no purpose,
 * as all the functionality is handled in
 * superclasses. However, the subclasses of
 * this class may require special behavior
 * such as logging in the user or expanding search results,
 * so later versions may include these added behaviors.
 * 
 * @see AbstractQueryGatherAutomation
 * @author Matt Crow
 */
public abstract class AbstractPeopleSoftAutomation extends AbstractQueryGatherAutomation{
    /**
     * 
     * @param autoName the name to display for this automation.
     * @param description the description of what this automation does
     * @param q the QueryManager which manages all the input data used by this object
     * @param r the ResultManager which handles the results of the automation's queries
     */
    public AbstractPeopleSoftAutomation(String autoName, String description, QueryManager q, ResultManager r) {
        super(autoName, description + " (notice: this automation only works for American River College staff with access to https://psreports.losrios.edu)", q, r);
    }
}
