package io;

/**
 *
 * @author Matt
 */
public class CsvFileRequirements extends FileRequirements{
    private final String[] headers;
    
    public CsvFileRequirements(String desc, String[] reqHeaders) {
        super(desc, FileType.CSV);
        headers = reqHeaders.clone();
    }
    
    public final String[] getReqHeaders(){
        return headers.clone();
    }
    
    @Override
    public final String reformatFile(String fileText){
        return new CsvParser(headers).reformat(fileText, false);
    }
}
