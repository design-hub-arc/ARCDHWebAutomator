package io;

import java.io.File;
import java.util.Arrays;

/**
 * The CsvFileRequirements class is used to control what
 * data is fed into automations, guaranteeing that their
 * input is formatted correctly.
 * 
 * @author Matt Crow
 */
public final class CsvFileRequirements {
    private final String reqDesc;
    private final String[] headers;
    private final CsvParser parser;
    
    /**
     * Defines the requirements for a file that this class should
     * approve.
     * 
     * @param desc a textual description of the requirements
     * files should meet. It should provide a clear description
     * to the user of how the file should be formatted.
     * @param reqHeaders the column headers that input files must have 
     */
    public CsvFileRequirements(String desc, String[] reqHeaders) {
        reqDesc = desc;
        headers = reqHeaders.clone();
        parser = new CsvParser(headers);
    }
    
    /**
     * Returns a textual description of the
     * type of file specified by these requirements.
     * 
     * @return 
     */
    public String getReqDesc(){
        return reqDesc;
    }
    
    public String[] getReqHeaders(){
        return headers.clone();
    }
    
    public boolean validateFile(File f) throws Exception {
        String name = f.getName();
        int idx = name.lastIndexOf('.');
        String ext = (idx == -1) ? "" : name.substring(idx + 1);
        if(!Arrays.stream(FileType.CSV.getExtensions()).anyMatch((ex)->ex.equalsIgnoreCase(ext))){
            throw new Exception("Wrong file type: " + ext + ". File must be one of the following: " + Arrays.toString(FileType.CSV.getExtensions()));
        }
        parser.reformat(FileReaderUtil.readFile(f)); //throws CsvFileException if it can't be reformatted
        return true;
    }
    
    /**
     * Converts the fileText given from a File approved by
     * the validateFile method into a specific format needed
     * by the program, then returns the reformatted file
     * text as a String.
     * 
     * @param fileText the unformatted text contents of a file
     * @return the reformatted file text
     */
    public final String reformatFile(String fileText){
        return new CsvParser(headers).reformat(fileText, false);
    }
}
