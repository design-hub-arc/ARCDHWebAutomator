package logging;

import io.FileSelector;
import io.ResultFileWriter;
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
    
    public void setOnEncounterError(Consumer<String> nomNom){
        onEncounterError = nomNom;
    }
    
    public void clear(){
        loggedErrors.delete(0, loggedErrors.length());
    }
    
    public void showPopup(){
        boolean errors = getLog().length() != 0;
        if(errors){
            String msg = 
                "Encountered the following errors:\n" 
                + getLog() 
                + "\n Would you like to download this message as a text file?";
            
            int r = JOptionPane.showConfirmDialog(null, msg, "Error Log", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if(r == JOptionPane.YES_OPTION){
                saveToFile();
            }
        }
        
        
    }
    
    public void saveToFile(){
        FileSelector.createNewFile((newFile)->{
            try {
                new ResultFileWriter().writeToFile(newFile, getLog());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
