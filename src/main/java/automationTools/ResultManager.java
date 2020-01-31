package automationTools;

import io.FileSelector;
import io.FileWriterUtil;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Matt
 */
public class ResultManager{
    private final AbstractAutomation hostingAutomation;
    private final String resultUrl;
    private final StringBuilder result;
    
    public ResultManager(AbstractAutomation forAutomation, String resultURL){
        hostingAutomation = forAutomation;
        resultUrl = resultURL;
        result = new StringBuilder();
    }
    
    public final String getResultUrl(){
        return resultUrl;
    }
    
    public final String getResult(){
        return result.toString();
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
    
    public void saveToFile(){
        log("Saving file");
        FileSelector.createNewFile("Where do you want to save the automation result?", (File f)->{
            log("Attempting to write to " + f.getAbsolutePath());
            try {
                FileWriterUtil.writeToFile(f, toString());
                log("file successfully written");
            } catch (IOException ex) {
                hostingAutomation.reportError(ex);
                log("failed to write to file");
            }
        });
    }
    
    public void log(String s) {
        hostingAutomation.writeOutput(s);
    }
    
    @Override
    public String toString(){
        return result.toString();
    }
}
