package io;

import java.io.File;

/**
 *
 * @author Matt Crow
 */
public class CsvFileRequirements extends FileRequirements{
    private final String[] headers;
    private final CsvParser parser;
    
    public CsvFileRequirements(String desc, String[] reqHeaders) {
        super(desc, FileType.CSV);
        headers = reqHeaders.clone();
        parser = new CsvParser(headers);
    }
    
    public final String[] getReqHeaders(){
        return headers.clone();
    }
    
    @Override
    public boolean validateFile(File f) throws Exception {
        super.validateFile(f);
        parser.reformat(new QueryFileReader().readFile(f)); //throws CsvFileException if it can't be reformatted
        return true;
    }
    
    @Override
    public final String reformatFile(String fileText){
        return new CsvParser(headers).reformat(fileText, false);
    }
}
