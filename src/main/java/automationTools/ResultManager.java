package automationTools;

import io.FileSelector;
import io.ResultFileWriter;
import java.io.File;
import java.io.IOException;
import logging.Logger;

/**
 *
 * @author Matt
 */
public class ResultManager implements Logger{
    private final String resultUrl;
    private final StringBuilder result;
    private final StringBuilder log;
    private Logger logger;
    
    public ResultManager(String resultURL){
        resultUrl = resultURL;
        result = new StringBuilder();
        log = new StringBuilder();
        logger = new Logger() {
            @Override
            public void log(String s) {
                System.out.println(s);
                log.append(s).append('\n');
            }

            @Override
            public String getLog() {
                return log.toString();
            }
        };
    }
    
    public final String getResultUrl(){
        return resultUrl;
    }
    
    public final void clear(){
        result.delete(0, result.length());
    }
    
    public final void append(String s){
        log("Appending to result: " + s);
        result.append(s);
        log("Result is now ");
        log(toString());
    }

    public void setLogger(Logger l){
        logger = l;
    }
    
    public void saveToFile(){
        log("Saving file");
        FileSelector.createNewFile((File f)->{
            log("Attempting to write to " + f.getAbsolutePath());
            try {
                new ResultFileWriter().writeToFile(f, toString());
                log("file successfully written");
            } catch (IOException ex) {
                ex.printStackTrace();
                log("failed to write to file");
            }
        });
    }
    
    @Override
    public String toString(){
        return result.toString();
    }

    @Override
    public void log(String s) {
        logger.log(s);
    }

    @Override
    public String getLog() {
        return logger.getLog();
    }
}
