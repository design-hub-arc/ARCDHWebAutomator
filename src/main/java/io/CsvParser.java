package io;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The CsvParser is used to format CSV strings.
 * 
 * @author Matt Crow
 */
public class CsvParser {
    public static final String NEW_LINE = System.lineSeparator();
    
    private final String[] reqHeaders;
    
    /**
     * Creates a CsvParser which will reformat Strings in the requested format.
     * reqHeaders is an array of String which shows it how to reformat files.
     * For example, given the CSV data <br>
     * 
     * h1, h2, h3, h4, h5 <br>
     *  1,  2,  3,  4,  5 <br>
     *  6,  7,  8,  9, 10 <br>
     * 
     * If reqHeaders is set to <br>
     * {@code 
     *  ["h3", "h1", "h2]
     * },<br>
     * calling reformat on the given data will rearrange it to<br>
     * 
     * h3, h1, h2<br>
     *  3,  1,  2<br>
     *  8,  6,  7<br>
     * 
     * @param reqHeaders the headers the source CSV file must contain, specifying what order to put columns in.
     */
    public CsvParser(String[] reqHeaders){
        this.reqHeaders = reqHeaders.clone();
    }
    
    /**
     * Converts the given fileText into the format specified by the
     * required headers passed to this' constructor. It also removes quote marks from the resulting string.
     * @param fileText text in CSV format.
     * @param includeHeaders if the resulting String should contain headers. Defaults to true. 
     * @return the newly formatted String.
     */
    public String reformat(String fileText, boolean includeHeaders){
        String[] lines = fileText.split(NEW_LINE);
        String[] headers = Arrays.stream(lines[0].split(",")).map((h)->h.trim()).toArray((l)->new String[l]);
        if(headers.length < reqHeaders.length){
            throw new CsvFileException("File does not contain enough headers. Must contain the headers " + Arrays.toString(reqHeaders));
        }
        
        //find which column each header occurs in
        HashMap<String, Integer> headerToCol = new HashMap<>();
        for(String header : reqHeaders){
            boolean found = false;
            //System.out.println("Searching for " + header);
            for(int i = 0; i < headers.length && !found; i++){
                //System.out.println("[" + headers[i] + "]");
                if(headers[i].equalsIgnoreCase(header)){
                    headerToCol.put(header, i);
                    //System.out.println("Found it!");
                    found = true;
                }
            }
            if(!found){
                throw new MissingHeaderException(header, headers);
            }
        }
        System.out.println("building new file...");
        //build the file
        StringBuilder newFile = new StringBuilder();
        for(int i = 0; includeHeaders && i < reqHeaders.length; i++){
            newFile.append(reqHeaders[i]);
            if(i != reqHeaders.length -1 ){
                newFile.append(",");
            }
        }
        String[] line;
        String data;
        for(int i = 1; i < lines.length; i++){
            //skip header
            newFile.append(NEW_LINE);
            line = lines[i].split(",");
            for(int j = 0; j < reqHeaders.length; j++){
                data = line[headerToCol.get(reqHeaders[j])];
                //remove quote marks
                data = data.replaceAll("'|\"", "");
                newFile.append(data);
                if(j != reqHeaders.length - 1){
                    newFile.append(", ");
                }
            }
        }
        
        String ret = newFile.toString();
        System.out.println("CsvParser returns this in CsvParser.java");
        System.out.println(ret);
        
        return ret;
    }
    public String reformat(String fileText){
        return reformat(fileText, true);
    }
    
    public static void main(String[] args) throws IOException{
        CsvParser cp = new CsvParser(new String[]{
            "h1",
            "h2",
            "h3"
        });
        String s = new QueryFileReader().readStream(CsvParser.class.getResourceAsStream("/testFile.csv"));
        System.out.println(s);
        try{
            String n = cp.reformat(s);
            //System.out.println(n);
        }catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}
