package io;

import java.io.File;
import java.util.function.Consumer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Matt
 */
public class FileSelector{
    private final JFileChooser chooser;
    private final Consumer<File> action;
    
    public FileSelector(FileType type, Consumer<File> act){
        chooser = new JFileChooser();
        chooser.setFileSelectionMode((type == FileType.DIR) ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter(type.getName(), type.getExtensions()));
        action = act;
    }
    
    public void chooseFile(){
        if(chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION){ 
            action.accept(chooser.getSelectedFile());
        }
    }
    
    public static void chooseCsvFile(Consumer<File> action){
        new FileSelector(FileType.CSV, action).chooseFile();
    }
    
    public static void chooseExeFile(Consumer<File> action){
        new FileSelector(FileType.EXE, action).chooseFile();
    }
    
    public static void createNewFile(Consumer<File> action){
        new FileSelector(FileType.DIR, (File f)->{
            String name = JOptionPane.showInputDialog(null, "What do you want to name this new file?");
            File newFile = new File(f.getAbsolutePath() + File.separator + name);
            action.accept(newFile);
        }).chooseFile();
    }
}
