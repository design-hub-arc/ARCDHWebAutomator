package csv;

import io.FileReaderUtil;
import java.io.IOException;
import java.util.Arrays;

/**
 * The CsvParser is used to convert CSV strings to a CsvFile object.
 * 
 * @author Matt Crow
 */
public class CsvParser {
    public static final String NEW_LINE = System.lineSeparator();
    
    public static String[] extractHeaders(String fileText){
        int nlIdx = fileText.indexOf(NEW_LINE);
        if(nlIdx != -1){
            //contains more than just 1 row
            fileText = fileText.substring(0, nlIdx); //chop off everything on or after the newline
        }
        String[] headers = fileText.split(",");
        return Arrays.stream(headers).map((header)->header.trim()).toArray((size)->new String[size]);
    }
    
    public static CsvFile toCsvFile(String fileText){
        CsvFile ret = new CsvFile();
        boolean debug = false;
        
        String[] lines = fileText.split(NEW_LINE);
        if(debug){
            Arrays.stream(lines).forEach((line)->System.out.println("Line: " + line));
        }
        //first line is headers
        if(lines.length >= 1){
            String[] headers = lines[0].split(",");
            for(String header : headers){
                ret.addHeader(header);
            }
        }
        
        //now build the body
        CsvRow row;
        for(int i = 1; i < lines.length; i++){
            row = new CsvRow(ret, lines[i]);
            ret.addRow(row);
        }
        
        if(debug){
            System.out.println("Created new CsvFile:");
            System.out.println(ret.toString());
        }
        
        return ret;
    }

    public static void main(String[] args) throws IOException{
        String s = FileReaderUtil.readStream(CsvParser.class.getResourceAsStream("/testFile.csv"));
        System.out.println(s);
        
        CsvFile f = CsvParser.toCsvFile(s);
        System.out.println(f.toString());
    }
}
