package io;

import io.FileType;
import io.FileWriterUtil;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The FileSelector class is used to more conveniently select files
 * than by having to manually construct a JFileChooser every time.
 * 
 * The class provides three static methods for easily allowing the
 * user to select a file:
 * <ul>
 * <li>chooseCsvFile</li>
 * <li>chooseExeFile</li>
 * <li>createNewFile</li>
 * </ul>
 * You can also use the FileSelector constructor to create
 * file selectors for other file types, or if you need specific behavior.
 * 
 * @author Matt Crow
 */
public class FileSelector{
    private final JFileChooser chooser;
    private final Consumer<File> action;
    
    /**
     * Creates a file selector which can select files of the specified type.
     * Upon invoking the chooseFile method, the user will be prompted to select a file.
     * If the user selects a valid file, it is passed to act.
     * @param text the text to display in the file selector popup
     * @param type the file type this should select.
     * @param act the action to run after the user selects a file.
     */
    public FileSelector(String text, FileType type, Consumer<File> act){
        chooser = new JFileChooser();
        chooser.setDialogTitle(text);
        chooser.setFileSelectionMode((type == FileType.DIR) ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
        if(!(type == FileType.EXE && System.getProperty("os.name").toLowerCase().contains("mac"))){
            //Mac chromedriver doesn't have extension, so skip this section for mac exe's
            chooser.setFileFilter(new FileNameExtensionFilter(type.getName(), type.getExtensions()));
        }
        action = act;
        chooser.setOpaque(true);
        chooser.setVisible(true);
    }
    
    public void chooseFile(){
        if(chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION){ 
            action.accept(chooser.getSelectedFile());
        }
    }
    
    public static void chooseCsvFile(String text, Consumer<File> action){
        new FileSelector(text, FileType.CSV, action).chooseFile();
    }
    
    public static void chooseExeFile(String text, Consumer<File> action){
        new FileSelector(text, FileType.EXE, action).chooseFile();
    }
    
    /**
     * Asks the user for a directory to save the new file to,
     * asks for a name for the file, 
     * and passes the newly created file to action.
     * @param action 
     */
    public static void createNewFile(String text, Consumer<File> action){
        new FileSelector(text, FileType.DIR, (File f)->{
            String name = JOptionPane.showInputDialog(null, "What do you want to name this new file?");
            if(name == null || name.isEmpty()){
                name = "name-not-set";
            }
            File newFile = new File(f.getAbsolutePath() + File.separator + name);
            action.accept(newFile);
        }).chooseFile();
    }
    
    public static void main(String[] args){
        createNewFile("Create a file here", (f)->{
            try {
                FileWriterUtil.writeToFile(f, "whatever");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
