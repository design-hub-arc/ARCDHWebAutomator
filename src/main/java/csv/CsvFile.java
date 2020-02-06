package csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * This class is used to work with CSV files.
 * It allows developers to work with table-like
 * data without having to guess whether or not
 * the data is properly formatted.
 * 
 * @author Matt Crow
 */
public class CsvFile {
    private final HashMap<String, Integer> headerCols;
    private final ArrayList<String> headers;
    private final ArrayList<CsvRow> rows;
    
    /**
     * Creates an empty CsvFile
     */
    public CsvFile(){
        headerCols = new HashMap<>();
        headers = new ArrayList<>();
        rows = new ArrayList<>();
    }
    
    /**
     * Creates a CsvFile with the given
     * headers.
     * 
     * @param h the headers to include in the new file
     */
    public CsvFile(String[] h){
        this();
        for(String header : h){
            addHeader(header);
        }
    }
    
    /**
     * Creates a smaller version of this file,
     * containing only columns with the specified
     * headers.
     * 
     * @param newHeaders the headers to include in the returned value.
     * @return a copy of this, but with fewer columns
     */
    public CsvFile getSubfile(String[] newHeaders){
        CsvFile ret = new CsvFile();
        
        //copy headers over
        for(String header : newHeaders){
            if(headerCols.containsKey(header)){
                ret.addHeader(header);
            } else {
                throw new MissingHeaderException(header, headers.toArray(new String[headers.size()]));
            }
        }
        
        //copy body over
        rows.forEach((row) -> {
            ret.addRow(row);
            //addRow automaticly reorganizes columns
        });
        
        return ret;
    }
    
    /**
     * Adds the given header to this file,
     * if it is not already present.
     * 
     * @param header the header to add.
     * @return this, for chaining purposes
     */
    public CsvFile addHeader(String header){
        if(headerCols.containsKey(header)){
            throw new IllegalArgumentException("This already has header " + header + ". Cannot duplicate headers");
        }
        headerCols.put(header, headers.size());
        headers.add(header);
        rows.forEach((row)->row.padValues());
        return this;
    }
    
    /**
     * Renames a column in this file. Note that the old header must
     * exist in the file, but the new header must not.
     * 
     * @param oldHeader
     * @param newHeader
     * @return this, for chaining purposes
     */
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
    
    /**
     * 
     * @return a copy of this' headers, in order.
     */
    public ArrayList<String> getHeaders(){
        return (ArrayList<String>)headers.clone();
    }
    /**
     * 
     * @param idx the index of the row to return
     * @return the idx-th row of this file's body
     */
    public CsvRow getRow(int idx){
        if(idx > 0 || idx <= rows.size()){
            throw new IllegalArgumentException("Cannot access row #" + idx);
        }
        return rows.get(idx);
    }
    /**
     * 
     * @return a copy of this' body
     */
    public ArrayList<CsvRow> getBody(){
        return (ArrayList<CsvRow>)rows.clone();
    }
    
    /**
     * 
     * @return the number of headers in this file 
     */
    public int getHeaderCount(){
        return headers.size();
    }
    
    /**
     * 
     * @return the number of rows in this' body,
     * not including headers
     */
    public int getRowCount(){
        return rows.size();
    }
    
    /**
     * Clears the contents
     * and headers of this
     * @return this, for chaining purposes
     */
    public CsvFile clear(){
        headers.clear();
        rows.clear();
        return this;
    }
    
    /**
     * Returns this CsvFile in
     * CSV format, suitable for
     * saving in a file somewhere.
     * 
     * @return 
     */
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
