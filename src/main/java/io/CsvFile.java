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
    private final ArrayList<CsvRow> rows;
    
    public CsvFile(){
        headers = new ArrayList<>();
        headerCols = new HashMap<>();
        rows = new ArrayList<>();
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
        rows.forEach((row)->row.padValues());
    }
    
    /**
     * Returns the index of the given header
     * in this file's header row. If the header
     * is not present, returns -1.
     * 
     * @param header the header to find the column of
     * @return the index of the given header.
     */
    public int getHeaderCol(String header){
        return headerCols.getOrDefault(header, -1);
    }
    
    public int getHeaderCount(){
        return headers.size();
    }
    
    public void addRow(CsvRow row){
        rows.add(row);
    }
    
    public CsvRow getRow(int idx){
        if(idx > 0 || idx <= rows.size()){
            throw new IllegalArgumentException("Cannot access row #" + idx);
        }
        return rows.get(idx);
    }
    
    /**
     * Clears the contents
     * and headers of this
     */
    public void clear(){
        headers.clear();
        rows.clear();
    }
    
    @Override
    public String toString(){
        StringBuilder b = new StringBuilder();
        String headerString = headers.stream().collect(Collectors.joining(","));
        b.append(headerString);
        rows.forEach((row)->{
            b.append('\n').append(row.toString());
        });
        return b.toString();
    }
    
    public static void main(String[] args){
        CsvFile f = new CsvFile();
        f.addHeader("a");
        f.addHeader("b");
        f.addHeader("c");
        CsvRow r = new CsvRow(f);
        r.set("b", "\"value\", and more");
        f.addRow(r);
        System.out.println(f.toString());
    }
}
