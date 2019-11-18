package io;

import java.io.File;
import java.util.function.Consumer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Matt
 */
public class FileSelector{
    private final JFileChooser chooser;
    private final Consumer<File> action;
    
    public static final String[] CSV = new String[]{"Comma Separated Values", "csv"};
    public static final String[] EXE = new String[]{"Executable file", "exe", "dmg"};
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
    
    public static void chooseCsvFile(Consumer<File> action){
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(jfc.showOpenDialog(jfc) == JFileChooser.APPROVE_OPTION){ 
            action.accept(jfc.getSelectedFile());
        }
    }
    
    public static void createNewFile(Consumer<File> action){
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = jfc.showOpenDialog(null);
        if(result == JFileChooser.APPROVE_OPTION){ 
            String name = JOptionPane.showInputDialog(null, "What do you want to name this new file?");
            File newFile = new File(jfc.getSelectedFile().getAbsolutePath() + File.separator + name);
            action.accept(newFile);
        }
    }
}
