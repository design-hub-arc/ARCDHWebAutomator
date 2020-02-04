package io;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 
 * @author Matt Crow
 */
public class CsvRow {
    private final CsvFile parent;
    private final ArrayList<String> values;
    
    public CsvRow(CsvFile inFile){
        parent = inFile;
        values = new ArrayList<>();
        padValues();
    }
    
    public CsvRow(CsvFile inFile, String[] cells){
        this(inFile);
        for(int i = 0; i < cells.length; i++){
            values.set(i, cells[i]);
        }
    }
    
    public CsvRow(CsvFile inFile, String row){
        this(inFile, row.split(","));
    }
    
    /**
     * Adds blank cells to this row
     * until it is as wide as its parent.
     */
    public void padValues(){
        int blanksToAdd = parent.getHeaderCount() - values.size();
        for(int i = 0; i < blanksToAdd; i++){
            values.add("");
        }
    }
    
    public void set(String columnHeader, String value){
        int idx = parent.getHeaderCol(columnHeader);
        if(idx == -1){
            throw new IllegalArgumentException("This CsvFile does not have the header " + columnHeader);
        }
        if(idx >= values.size()){
            padValues();
        }
        values.set(idx, value);
    }
    
    public String get(String columnHeader){
        int idx = parent.getHeaderCol(columnHeader);
        if(idx == -1){
            throw new IllegalArgumentException("This CsvFile does not have the header " + columnHeader);
        }
        if(idx >= values.size()){
            padValues();
        }
        return values.get(idx);
    }
    
    /**
     * 
     * @return this row, as it would appear in a CSV file
     */
    @Override
    public String toString(){
        return values
            .stream()
            .map((String cell)->{
                return (cell.contains(",") && !(cell.startsWith("\"") && cell.endsWith("\""))) ? "\"" + cell + "\"" : cell;
            })
            .collect(Collectors.joining(","));
    }
}
