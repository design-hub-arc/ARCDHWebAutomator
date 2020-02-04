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
    private final String[] reqHeaders;
    
    /**
     * Defines the requirements for a file that this class should
     * approve.
     * 
     * @param desc a textual description of the requirements
     * files should meet. It should provide a clear description
     * to the user of how the file should be formatted.
     * @param reqHeaders the column reqHeaders that input files must have 
     */
    public CsvFileRequirements(String desc, String[] reqHeaders) {
        reqDesc = desc;
        this.reqHeaders = reqHeaders.clone();
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
        return reqHeaders.clone();
    }
    
    public boolean validateFile(File f) throws Exception {
        String name = f.getName();
        int idx = name.lastIndexOf('.');
        String ext = (idx == -1) ? "" : name.substring(idx + 1);
        if(!Arrays.stream(FileType.CSV.getExtensions()).anyMatch((ex)->ex.equalsIgnoreCase(ext))){
            throw new Exception("Wrong file type: " + ext + ". File must be one of the following: " + Arrays.toString(FileType.CSV.getExtensions()));
        }
        
        //see if it has the required headers
        String fileText = FileReaderUtil.readFile(f);
        int newlineIdx = fileText.indexOf(CsvParser.NEW_LINE);
        if(newlineIdx == -1){
            throw new IllegalArgumentException("The file provided has no body content, just headers");
        }
        String[] headers = fileText.substring(0, newlineIdx).split(",");
        boolean found;
        for(String reqHeader : reqHeaders){
            found = false;
            for(int i = 0; i < headers.length && !found; i++){
                if(reqHeader.equals(headers[i])){
                    found = true;
                }
            }
            if(!found){
                throw new MissingHeaderException(reqHeader, headers);
            }
        }
        
        return true;
    }
}
