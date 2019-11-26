package automationTools;

import static io.CsvParser.NEW_LINE;
import io.FileRequirements;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import logging.Logger;

/**
 *
 * @author Matt
 */
public class QueryManager implements Logger{
    private final FileRequirements fileReqs;
    private final LinkedList<String> queryFile;
    private final StringBuilder queryLog;
    private Logger log;
    private final String queryUrl;
    
    /**
     * 
     * @param inputUrl the URL this QueryManager should input queries into
     * @param reqs the requirements that query 
     * files fed into this must adhere to.
     */
    public QueryManager(String inputUrl, FileRequirements reqs){
        fileReqs = reqs;
        queryFile = new LinkedList<>();
        queryLog = new StringBuilder();
        queryUrl = inputUrl;
        log = new Logger() {
            @Override
            public void log(String s) {
                queryLog.append(s).append('\n');
                System.out.println(s);
            }

            @Override
            public String getLog() {
                return queryLog.toString();
            }
        };
    }
    
    /**
     * Used to obtain the requirements that query
     * files submitted to this class must follow.
     * 
     * @return 
     */
    public final FileRequirements getQueryFileReqs(){
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
    
    public void setLogger(Logger l){
        log = l;
    }

    @Override
    public void log(String s) {
        log.log(s);
    }

    @Override
    public String getLog() {
        return log.getLog();
    }
}
