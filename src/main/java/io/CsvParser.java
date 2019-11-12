package io;

import automations.AbstractAutomation;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Matt
 */
public class CsvParser {
    private final String[] requiredHeaders;
    
    public CsvParser(String[] reqHeaders){
        requiredHeaders = reqHeaders.clone();
    }
    
    public String reformat(String fileText){
        String[] lines = fileText.split(AbstractAutomation.NEW_LINE);
        String[] headers = lines[0].split(",");
        if(headers.length < requiredHeaders.length){
            throw new IllegalArgumentException("File does not contain enough headers. Must contain the headers " + Arrays.toString(requiredHeaders));
        }
        
        //find which column each header occurs in
        HashMap<String, Integer> headerToCol = new HashMap<>();
        Arrays.stream(requiredHeaders).forEach((header)->{
            boolean found = false;
            for(int i = 0; i < headers.length && !found; i++){
                if(headers[i].equalsIgnoreCase(header)){
                    headerToCol.put(header, i);
                    found = true;
                }
            }
            if(!found){
                throw new IllegalArgumentException("File is missing header " + header);
            }
        });
        
        //build the file
        StringBuilder newFile = new StringBuilder();
        for(int i = 0; i < requiredHeaders.length - 1; i++){
            // don't include last header yet
            newFile.append(requiredHeaders[i]).append(", ");
        }
        newFile.append(requiredHeaders[requiredHeaders.length - 1]);
        String[] line;
        String data;
        for(int i = 1; i < lines.length; i++){
            //skip header
            newFile.append("\n");
            line = lines[i].split(",");
            for(int j = 0; j < requiredHeaders.length; j++){
                data = line[headerToCol.get(requiredHeaders[j])];
                //remove quote marks
                data = data.replaceAll("'|\"", "");
                newFile.append(data);
                if(j != requiredHeaders.length - 1){
                    newFile.append(", ");
                }
            }
        }
        
        String ret = newFile.toString();
        System.out.println("CsvParser returns this in CsvParser.java");
        System.out.println(ret);
        
        return ret;
    }
}
