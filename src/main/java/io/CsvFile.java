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
    
    public CsvFile addHeader(String header){
        if(headerCols.containsKey(header)){
            throw new IllegalArgumentException("This already has header " + header + ". Cannot duplicate headers");
        }
        headerCols.put(header, headers.size());
        headers.add(header);
        rows.forEach((row)->row.padValues());
        return this;
    }
    
    public CsvFile renameColumn(String oldHeader, String newHeader){
        if(!headerCols.containsKey(oldHeader)){
            throw new IllegalArgumentException("Cannot rename column " + oldHeader + ", as it does not exists in the CsvFile");
        }
        if(headerCols.containsKey(newHeader)){
            throw new IllegalArgumentException("CsvFile already has a column named " + newHeader);
        }
        headers.set(headerCols.get(oldHeader), newHeader);
        headerCols.put(newHeader, headerCols.get(oldHeader));
        return this;
    }
    
    public ArrayList<String> getHeaders(){
        return headers;
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
    
    /**
     * Creates a copy of the given row,
     * and adds it to this CsvFile.
     * The columns of the row are rearranged
     * to match those of this.
     * 
     * @param row 
     */
    public void addRow(CsvRow row){
        CsvRow newRow = new CsvRow(this);
        headers.forEach((header)->{
            newRow.set(header, row.get(header));
        });
        rows.add(newRow);
    }
    
    public CsvRow getRow(int idx){
        if(idx > 0 || idx <= rows.size()){
            throw new IllegalArgumentException("Cannot access row #" + idx);
        }
        return rows.get(idx);
    }
    
    public ArrayList<CsvRow> getBody(){
        return (ArrayList<CsvRow>)rows.clone();
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
        
        CsvFile otherFile = new CsvFile();
        otherFile.addHeader("a").addHeader("d").addHeader("c").addHeader("b");
        r = new CsvRow(otherFile);
        r.set("a", "a column");
        r.set("b", "second");
        r.set("c", "third");
        r.set("d", "don't include this");
        
        f.addRow(r);
        
        System.out.println(f.toString());
    }
}
