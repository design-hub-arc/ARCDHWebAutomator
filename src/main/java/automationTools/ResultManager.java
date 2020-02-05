package automationTools;

import io.CsvFile;
import io.CsvRow;
import io.FileSelector;
import io.FileWriterUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * @author Matt
 */
public class ResultManager{
    private final AbstractAutomation hostingAutomation;
    private final String resultUrl;
    private final CsvFile result;
    
    public ResultManager(AbstractAutomation forAutomation, String resultURL){
        hostingAutomation = forAutomation;
        resultUrl = resultURL;
        result = new CsvFile();
    }
    
    public final String getResultUrl(){
        return resultUrl;
    }
    
    public final CsvFile getCsvFile(){
        return result;
    }
    
    public final String getResultString(){
        return result.toString();
    }
    
    public final void clear(){
        result.clear();
    }
    
    public final void append(ArrayList<CsvRow> rows){
        String txt = rows.stream().map((row)->row.toString()).collect(Collectors.joining("\n"));
        log("Appending to result: \n" + txt);
        rows.forEach((row)->result.addRow(row));
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
