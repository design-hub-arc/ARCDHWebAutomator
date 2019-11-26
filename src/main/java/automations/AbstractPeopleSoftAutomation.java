package automations;

/**
 * This class serves as the base for automations
 * for the PeopleSoft financial website.
 * The automations follow this sequence:
 * 1. take a data source file, usually providing values to fill into fields in a web page, and use it as a list of queries.
 * 2. Go to a designated start page.
 * 3. repeat steps 4-6 until the list of queries is empty:
 * 4. if the browser is on the given input URL, dequeue the next query, and fill out a form on the webpage, then submit the form.
 * 5. if the browser is on the given result URL, read the result, and append it to the accumulated results of all previous queries.
 * 6. return to the input URL.
 * 7. Write the combined results of every query to a file on the user's computer.
 * @author Matt Crow
 */
public abstract class AbstractPeopleSoftAutomation extends QueryGatherAutomation{
    /**
     * 
     * @param n the name to display for this automation.
     * @param desc the description of what this automation does
     * @param resultUrl the URL of the webpage where this should read the result of its query
     * @param q the QueryManager which manages all the input data used by this object
     */
    public AbstractPeopleSoftAutomation(String n, String desc, String resultUrl, QueryManager q){
        super(n, desc, q, resultUrl);
    }
}
