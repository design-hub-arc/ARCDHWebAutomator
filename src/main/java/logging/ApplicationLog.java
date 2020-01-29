package logging;

import application.Application;
import java.util.ArrayList;

/**
 * The ApplicationLog is used to record the actions
 * of this application. This allows the program to
 * create log files, which greatly ease debugging
 * @author Matt
 */
public class ApplicationLog implements Logger{
    private final Application forApp;
    private final StringBuilder log;
    private final ArrayList<ErrorLogListener> errorListeners;
    
    public ApplicationLog(Application app){
        forApp = app;
        log = new StringBuilder();
        errorListeners = new ArrayList<>();
    }
    
    public void addErrorListener(ErrorLogListener listener){
        errorListeners.add(listener);
    }

    @Override
    public void log(String s) {
        log.append(s).append('\n');
    }
    
    public void logError(String errMsg){
        log.append(errMsg).append('\n');
        errorListeners.forEach((listener)->listener.errorLogged(this, errMsg));
    }
    
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
    
    /*
    public void clear(){
        log.delete(0, log.length());
        errorListeners.forEach((listener)->listener.logCleared(this));
    }*/

    @Override
    public String getLog() {
        return log.toString();
    }
}
