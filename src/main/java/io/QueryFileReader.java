package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import util.SafeString;

/**
 * Might change this to an abstract class later
 * @author Matt
 */
public class QueryFileReader {
    public SafeString readStream(InputStream s) throws IOException{
        SafeString ret = new SafeString(new char[0], 1000);
        
        InputStreamReader read = new InputStreamReader(s);
        char[] temp = new char[100];
        int numCharsRead = 0;
        while(read.ready()){
            numCharsRead = read.read(temp);
            if(numCharsRead != temp.length){
                char[] newTemp = new char[numCharsRead];
                System.arraycopy(temp, 0, newTemp, 0, numCharsRead);
                temp = newTemp;
            }
            //System.out.println(Arrays.toString(temp));
            
            ret.append(temp);
        }
        
        return ret;
    }
    
    public SafeString readFile(File f) throws FileNotFoundException, IOException{
        return readStream(new FileInputStream(f));
    }
    
    public static void main(String[] args) throws IOException{
        InputStream in = QueryFileReader.class.getResourceAsStream("/testFile.csv");
        SafeString result = new QueryFileReader().readStream(in);
        result.print();
    }
}
