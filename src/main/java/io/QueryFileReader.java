package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static io.CsvParser.NEW_LINE;

/**
 * Might change this to an abstract class later
 * @author Matt
 */
public class QueryFileReader {
    
    /**
     * Reads the given input stream, and returns its contents
     * as a String. Note that all newlines are replaced with '\n',
     * so you needn't concern yourself with Windows' carriage return.
     * 
     * @param s the InputStream to convert to a String
     * @return the contents of s, converted to a String
     * @throws IOException 
     */
    public String readStream(InputStream s) throws IOException{
        StringBuilder ret = new StringBuilder();
        
        BufferedReader read = new BufferedReader(new InputStreamReader(s));
        while(read.ready()){
            ret.append(read.readLine()).append(NEW_LINE);
        }
        read.close();
        
        return ret.toString();
    }
    
    public String readFile(File f) throws FileNotFoundException, IOException{
        return readStream(new FileInputStream(f));
    }
    
    public static void main(String[] args) throws IOException{
        InputStream in = QueryFileReader.class.getResourceAsStream("/testFile.csv");
        String result = new QueryFileReader().readStream(in);
        System.out.println(result);
    }
}
