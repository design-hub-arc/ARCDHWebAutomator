package io;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Matt
 */
public class CsvParser {
    public static final String NEW_LINE = System.lineSeparator();
    
    private final String[] reqHeaders;
    
    public CsvParser(String[] reqHeaders){
        this.reqHeaders = reqHeaders.clone();
    }
    
    public String reformat(String fileText){
        String[] lines = fileText.split(NEW_LINE);
        String[] headers = Arrays.stream(lines[0].split(",")).map((h)->h.trim()).toArray((l)->new String[l]);
        if(headers.length < reqHeaders.length){
            throw new IllegalArgumentException("File does not contain enough headers. Must contain the headers " + Arrays.toString(reqHeaders));
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
                throw new IllegalArgumentException("File is missing header " + header);
            }
        }
        System.out.println("building new file...");
        //build the file
        StringBuilder newFile = new StringBuilder();
        for(int i = 0; i < reqHeaders.length - 1; i++){
            // don't include last header yet
            newFile.append(reqHeaders[i]).append(", ");
        }
        newFile.append(reqHeaders[reqHeaders.length - 1]);
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
