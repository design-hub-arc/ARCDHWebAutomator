package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Matt
 */
public class ResultFileWriter {
    public void writeStream(OutputStream os, String s) throws IOException{
        OutputStreamWriter write = new OutputStreamWriter(os);
        write.write(s);
        write.flush();
        write.close();
    }
    
    public void writeToFile(File f, String s) throws FileNotFoundException, IOException{
        writeStream(new FileOutputStream(f), s);
    }
    
    public static void main(String[] args) throws IOException{
        String s = new QueryFileReader().readStream(ResultFileWriter.class.getResourceAsStream("/testFile.csv"));
        FileSelector fs = new FileSelector(FileSelector.DIR, (File f)->{
            String name = JOptionPane.showInputDialog(null, "What do you want to name this new file?");      
            File newFile = new File(f.getAbsolutePath() + File.separator + name);
            
            try {
                new ResultFileWriter().writeToFile(newFile, s);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        
        fs.chooseFile();
    }
}
