package logging;

import gui.ErrorPopup;
import io.FileSelector;
import io.FileWriterUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Matt
 */
public class ErrorLogger implements Logger{
    private final StringBuilder loggedErrors;
    private final ArrayList<ErrorLogListener> errorLogListeners;
    
    public ErrorLogger(){
        loggedErrors = new StringBuilder();
        errorLogListeners = new ArrayList<>();
        addErrorLogListener(new ErrorLogListener(){
            @Override
            public void errorLogged(ErrorLogger log, String msg){
                System.err.println(msg);
            }
            
            @Override
            public void logCleared(ErrorLogger log){};
        });
    }
    
    public void log(Exception ex){
        StringBuilder errMsg = new StringBuilder();
        errMsg.append("Encountered the following error: ").append(ex.toString()).append('\n');
        for(StackTraceElement stack : ex.getStackTrace()){
            errMsg.append("- ").append(stack.toString()).append('\n');
        }
        log(errMsg.toString());
    }
    
    @Override
    public void log(String s) {
        loggedErrors.append(s).append('\n');
        errorLogListeners.forEach((listener)->listener.errorLogged(this, s));
    }

    @Override
    public String getLog() {
        return loggedErrors.toString();
    }
    
    /**
     * 
     * @return whether or not an error has been logged in this. 
     */
    public boolean hasLoggedError(){
        return getLog().length() != 0;
    }
    
    /**
     * Adds a listener to this log. The listener will be notified
     * whenever this logs an error.
     * 
     * @param listener 
     */
    public void addErrorLogListener(ErrorLogListener listener){
        errorLogListeners.add(listener);
    }
    
    public void clear(){
        loggedErrors.delete(0, loggedErrors.length());
        errorLogListeners.forEach((listener)->listener.logCleared(this));
    }
    
    public void showPopup(){
        if(hasLoggedError()){
            String msg = 
                "Encountered the following errors:\n" 
                + getLog();
                
            new ErrorPopup(msg);
        } else {
            JOptionPane.showMessageDialog(null, "No errors to report");
        }
    }
    
    /**
     * Writes the content of this error log to the given file
     * @param f the file to write to.
     * @throws IOException if FileWriterUtil cannot write to the
     * given file
     */
    public void saveToFile(File f) throws IOException{
        FileWriterUtil.writeToFile(f, getLog());
    }
    
    public void saveToFile(){
        FileSelector.createNewFile("Where do you want to save the error log?", (newFile)->{
            try {
                saveToFile(newFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
