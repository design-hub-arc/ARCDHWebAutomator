package automationTools;

import csv.CsvFile;
import io.FileSelector;
import io.FileWriterUtil;
import java.io.IOException;

/**
 * ReadingAutomation should be implemented by automations
 * which read and store information from a webpage.
 * 
 * @see AbstractQueryGatherAutomation
 * 
 * @author Matt Crow
 */
public interface ReadingAutomation {
    
    /**
     * 
     * @return the URL where this should attempt to read data from
     */
    public abstract String getResultUrl();
    
    /**
     * This method should return the CsvFile
     * where this automation is storing data
     * accumulated during its current run.
     * @return 
     */
    public abstract CsvFile getResultFile();
    
    public default void saveResultToFile(){
        AbstractAutomation auto = 
            (this instanceof AbstractAutomation) 
            ? (AbstractAutomation)this 
            : null;
        
        if(auto != null){
            auto.writeOutput("Saving file:\n" + getResultFile().toString());
        }
        FileSelector.createNewFile("Where do you want to save the automation result?", (f)->{
            if(auto != null){
                auto.writeOutput("Attempting to write to " + f.getAbsolutePath());
            }
            try {
                FileWriterUtil.writeToFile(f, getResultFile().toString());
                if(auto != null){
                    auto.writeOutput("file written successfully");
                }
            } catch (IOException ex) {
                if(auto != null){
                    auto.reportError(ex);
                    auto.reportError("failed to write to file");
                }
            }
        });
    }
}
