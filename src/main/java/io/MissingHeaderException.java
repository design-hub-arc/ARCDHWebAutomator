package io;

import java.util.Arrays;

/**
 *
 * @author Matt
 */
public class MissingHeaderException extends CsvFileException{
    
    public MissingHeaderException(String reqHeader, String[] foundInstead){
        super(
            String.format(
                "Text is missing the header \"%s\". Instead, it has the headers %s", 
                reqHeader, 
                Arrays.toString(foundInstead)
            )
        );
    }
}
