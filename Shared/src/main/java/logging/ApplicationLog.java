package logging;

import java.util.ArrayList;
import logging.ErrorListener;
import logging.ErrorLogger;

/**
 * The ApplicationLog is used to record the actions
 * of this application. This allows the program to
 * create log files, which greatly ease debugging
 * @author Matt
 */
public class ApplicationLog implements ErrorLogger{
    private final StringBuilder log;
    private final ArrayList<ErrorListener> errorListeners;
    private boolean hasLoggedError;
    
    public ApplicationLog(){
        log = new StringBuilder();
        errorListeners = new ArrayList<>();
        hasLoggedError = false;
    }
    
    public void addErrorListener(ErrorListener listener){
        errorListeners.add(listener);
    }

    @Override
    public void log(String s) {
        log.append(s).append('\n');
    }
    
    @Override
    public void logError(String errMsg){
        log.append(errMsg).append('\n');
        hasLoggedError = true;
        errorListeners.forEach((listener)->listener.errorLogged(this, errMsg));
    }
    
    @Override
    public void logError(Exception ex){
        StringBuilder errMsg = new StringBuilder();
        errMsg
            .append("Encountered the following error: ")
            .append(ex.toString())
            .append('\n');
        for(StackTraceElement frame : ex.getStackTrace()){
            errMsg.append("- ").append(frame.toString()).append('\n');
        }
        logError(errMsg.toString());
    }
    
    
    public void clearFlags(){
        hasLoggedError = false;
        errorListeners.forEach((listener)->listener.logCleared(this));
    }
    
    @Override
    public boolean hasLoggedError(){
        return hasLoggedError;
    }

    @Override
    public String getLog() {
        return log.toString();
    }
}
