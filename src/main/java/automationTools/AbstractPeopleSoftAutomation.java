package automationTools;

import io.FileRequirements;

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
    
    public AbstractPeopleSoftAutomation(String autoName, String description, String inputUrl, FileRequirements reqs, String resultUrl) {
        super(
            autoName, 
            description + " (notice: this automation only works for American River College staff with access to https://psreports.losrios.edu)", 
            inputUrl, 
            reqs,
            resultUrl
        );
    }
}
