package io;

import java.io.File;
import java.util.function.Consumer;
import javax.swing.JFileChooser;

/**
 *
 * @author Matt
 */
public class FileSelector{
    private final JFileChooser chooser;
    private final Consumer<File> action;
    
    public static final String[] CSV = new String[]{"Comma Separated Values", "csv"};
    public static final String[] DIR = new String[]{"Directory", "Folder"};
    
    public FileSelector(String[] types, Consumer<File> act){
        chooser = new JFileChooser();
        chooser.setFileSelectionMode((types == DIR) ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
        action = act;
    }
    
    public void chooseFile(){
        if(chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION){ 
            action.accept(chooser.getSelectedFile());
        }
    }
}
