/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import util.SafeString;

/**
 *
 * @author Matt
 */
public class ResultFileWriter {
    public void writeStream(OutputStream os, SafeString ss) throws IOException{
        OutputStreamWriter write = new OutputStreamWriter(os);
        char[] vals = ss.toCharArray();
        write.write(vals);
        write.flush();
        write.close();
    }
    
    public void writeToFile(File f, SafeString ss) throws FileNotFoundException, IOException{
        writeStream(new FileOutputStream(f), ss);
    }
    
    public static void main(String[] args) throws IOException{
        SafeString ss = new QueryFileReader().readStream(ResultFileWriter.class.getResourceAsStream("/testFile.csv"));
        FileSelector fs = new FileSelector(FileSelector.DIR, (File f)->{
            String name = JOptionPane.showInputDialog(null, "What do you want to name this new file?");      
            File newFile = new File(f.getAbsolutePath() + File.separator + name);
            
            try {
                new ResultFileWriter().writeToFile(newFile, ss);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        
        fs.chooseFile();
    }
}
