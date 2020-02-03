package io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * This class is used to work with CSV files
 * @author Matt Crow
 */
public class CsvFile {
    private final ArrayList<String> headers;
    private final HashMap<String, Integer> headerCols;
    
    public CsvFile(){
        headers = new ArrayList<>();
        headerCols = new HashMap<>();
    }
    public CsvFile(String[] h){
        this();
        for(String header : h){
            addHeader(header);
        }
    }
    
    public void addHeader(String header){
        if(headerCols.containsKey(header)){
            throw new IllegalArgumentException("This already has header " + header + ". Cannot duplicate headers");
        }
        headerCols.put(header, headers.size());
        headers.add(header);
    }
    
    @Override
    public String toString(){
        StringBuilder b = new StringBuilder();
        String headerString = headers.stream().collect(Collectors.joining(","));
        b.append(headerString);
        
        //todo: append each row, with newline at the beginning
        
        return b.toString();
    }
    
    public static void main(String[] args){
        CsvFile f = new CsvFile();
        f.addHeader("a");
        f.addHeader("b");
        f.addHeader("c");
        System.out.println(f.toString());
    }
}
