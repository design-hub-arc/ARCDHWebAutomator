package logging;

import gui.ErrorPopup;
import io.FileSelector;
import io.FileWriterUtil;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.JOptionPane;

/**
 *
 * @author Matt
 */
public class ErrorLogger implements Logger{
    private final StringBuilder loggedErrors;
    private Consumer<String> onEncounterError;
    
    public ErrorLogger(){
        loggedErrors = new StringBuilder();
        onEncounterError = (s)->{
            System.err.println(s);
        };
    }
    
    public void log(Exception ex){
        log("Encountered the following error: " + ex.toString());
        for(StackTraceElement stack : ex.getStackTrace()){
            log("- " + stack.toString());
        }
    }
    
    @Override
    public void log(String s) {
        loggedErrors.append(s).append('\n');
        onEncounterError.accept(s);
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
    
    public void setOnEncounterError(Consumer<String> nomNom){
        onEncounterError = nomNom;
    }
    
    public void clear(){
        loggedErrors.delete(0, loggedErrors.length());
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
