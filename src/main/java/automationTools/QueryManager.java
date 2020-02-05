package automationTools;

import io.CsvFile;
import io.CsvFileRequirements;
import io.CsvRow;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * The QueryManager is used by automations implementing
 * the QueryingAutomation interface. The QueryManager
 * maintains a list of queries the automation should input.
 * Prior to running an automation, the user specifies a
 * query file, which must conform to the FileRequirements
 * of this QueryManager. The FileRequirements will usually
 * reformat files fed into this class, such as a rearranging
 * columns in a CSV file.
 * 
 * @see FileRequirements
 * 
 * @author Matt Crow
 */
public class QueryManager {
    private final AbstractAutomation hostingAutomation;
    private final CsvFileRequirements fileReqs;
    private final LinkedList<CsvRow> queryFile;
    private final String queryUrl;
    
    /**
     * 
     * @param forAutomation the automation this is saving queries for
     * @param inputUrl the URL this QueryManager should input queries into
     * @param reqs the requirements that query 
     * files fed into this must adhere to.
     */
    public QueryManager(AbstractAutomation forAutomation, String inputUrl, CsvFileRequirements reqs){
        hostingAutomation = forAutomation;
        fileReqs = reqs;
        queryFile = new LinkedList<>();
        queryUrl = inputUrl;
    }
    
    /**
     * Used to obtain the requirements that query
     * files submitted to this class must follow.
     * 
     * @return 
     */
    public final CsvFileRequirements getQueryFileReqs(){
        return fileReqs;
    }
    
    /**
     * 
     * @return the URL this object's
     * queries are meant to be put into.
     */
    public final String getInputUrl(){
        return queryUrl;
    }
    
    public final boolean isEmpty(){
        return queryFile.isEmpty();
    }
    
    public final void setQueryFile(CsvFile file){
        queryFile.clear();
        file.getBody().forEach((line)->{
            queryFile.add(line);
        });
    }
    
    public final CsvRow getNextQuery(){
        if(queryFile.isEmpty()){
            throw new NoSuchElementException("Query file is empty.");
        }
        
        log("Before Dequeing:");
        queryFile.forEach((query)->{
            log(query.toString());
        });
        
        CsvRow nextQuery = queryFile.removeFirst();
        
        log("After Dequeing:");
        queryFile.forEach((query)->{
            log("* " + query.toString());
        });
        log("Dequed query:\n" + nextQuery.toString());
        
        return nextQuery;
    }
    
    public void log(String s) {
        hostingAutomation.writeOutput(s);
    }
}
