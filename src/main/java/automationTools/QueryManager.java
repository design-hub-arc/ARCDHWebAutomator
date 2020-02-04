package automationTools;

import io.CsvFileRequirements;
import static io.CsvParser.NEW_LINE;
import java.util.Arrays;
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
    private final LinkedList<String> queryFile;
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
    
    public final void setQueryFile(String fileText){
        queryFile.clear();
        fileText = fileReqs.reformatFile(fileText);
        Arrays.stream(fileText.split(NEW_LINE)).forEach((s)->{
            queryFile.add(s);
        });
    }
    
    public final String getNextQuery(){
        if(queryFile.isEmpty()){
            throw new NoSuchElementException("Query file is empty.");
        }
        
        log("Before Dequeing:");
        queryFile.forEach((query)->{
            log(query);
        });
        
        String nextQuery = queryFile.removeFirst();
        
        log("After Dequeing:");
        queryFile.forEach((query)->{
            log("* " + query);
        });
        log("Dequed query:\n" + nextQuery);
        
        return nextQuery;
    }
    
    public void log(String s) {
        hostingAutomation.writeOutput(s);
    }
}
