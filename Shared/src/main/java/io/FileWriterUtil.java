package io;

import io.FileReaderUtil;
import io.FileSelector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * The FileWriterUtil is used to easily write Strings to a file or outputstream.
 * 
 * @author Matt Crow
 */
public class FileWriterUtil {
    public static void writeStream(OutputStream os, String s) throws IOException{
        OutputStreamWriter write = new OutputStreamWriter(os);
        write.write(s);
        write.flush();
        write.close();
    }
    
    public static void writeToFile(File f, String s) throws FileNotFoundException, IOException{
        writeStream(new FileOutputStream(f), s);
    }
    
    public static void main(String[] args) throws IOException{
        String s = FileReaderUtil.readStream(FileWriterUtil.class.getResourceAsStream("/testFile.csv"));
        FileSelector.createNewFile("Where do you want to copy testFile.csv to?", (File newFile)->{
            try {
                FileWriterUtil.writeToFile(newFile, s);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
