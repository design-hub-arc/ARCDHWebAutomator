package io;

import java.io.File;
import java.util.Arrays;

/**
 * The FileRequirements class is used to
 * ensure that files conform to the requirements
 * of the automations using them.
 * 
 * This could potentially be merged into CsvFileRequirements,
 * assuming we only use CSV files. Furthermore, the functionality
 * of this class could be moved to QueryManager, but that might
 * be too cluttered.
 * 
 * @author Matt Crow
 */
public abstract class FileRequirements {
    private final String reqDesc;
    private final FileType type;
    
    /**
     * Defines the requirements for a file that this class should
     * approve.
     * 
     * @param desc a textual description of the requirements
     * files should meet. It should provide a clear description
     * to the user of how the file should be formatted.
     * 
     * @param fileType 
     */
    public FileRequirements(String desc, FileType fileType){
        reqDesc = desc;
        type = fileType;
    }
    
    /**
     * Returns a textual description of the
     * type of file specified by these requirements.
     * 
     * @return 
     */
    public final String getReqDesc(){
        return reqDesc;
    }
    
    /**
     * 
     * @return the required file type 
     */
    public final FileType getReqType(){
        return type;
    }

    public boolean validateFile(File f) throws Exception{
        String name = f.getName();
        int idx = name.lastIndexOf('.');
        String ext = (idx == -1) ? "" : name.substring(idx + 1);
        if(!Arrays.stream(type.getExtensions()).anyMatch((ex)->ex.equalsIgnoreCase(ext))){
            throw new Exception("Wrong file type: " + ext + ". File must be one of the following: " + Arrays.toString(type.getExtensions()));
        }
        return true;
    };
    
    /**
     * Converts the fileText given from a File approved by
     * the validateFile method into a specific format needed
     * by the program, then returns the reformatted file
     * text as a String.
     * 
     * @param fileText the unformatted text contents of a file
     * @return the reformatted file text
     */
    public abstract String reformatFile(String fileText);

}
