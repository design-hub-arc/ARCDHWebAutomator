package automationTools;

import csv.CsvFile;
import io.FileSelector;
import io.FileWriterUtil;
import java.io.IOException;
import logging.Logger;

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
        Logger.log("ReadingAutomation.saveResultToFile", "Saving file:\n" + getResultFile().toString());
        FileSelector.createNewFile("Where do you want to save the automation result?", (f)->{
            Logger.log("ReadingAutomation.saveResultToFile", "Attempting to write to " + f.getAbsolutePath());
            
            try {
                FileWriterUtil.writeToFile(f, getResultFile().toString());
                Logger.log("ReadingAutomation.saveResultToFile", "file written successfully");
            } catch (IOException ex) {
                Logger.logError("ReadingAutomation.saveResultToFile", ex);
                Logger.logError("ReadingAutomation.saveResultToFile", "failed to write to file");
            }
        });
    }
}
