package io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import logging.Logger;
import main.EntryPoint;

/**
 * The FileSystem class is used
 * to interface with the folder containing
 * resources utilized by the program, such as
 * webdrivers, plugins, and more.
 * This provides a single location where the program
 * can find the resources it needs,
 * minimizing the need for users to select files.
 * 
 * @author Matt Crow
 */
public final class FileSystem {
    private final EntryPoint forApp;
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String ARCDH_FOLDER_PATH = USER_HOME + File.separator + "ARCDH";
    public static final String APP_FOLDER_PATH = ARCDH_FOLDER_PATH + File.separator + "WebAutomator";
    public static final String LOG_FOLDER_PATH = APP_FOLDER_PATH + File.separator + "logs";
    public static final String JAR_FOLDER_PATH = APP_FOLDER_PATH + File.separator + "bin";
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-DD-uuuu_hh_mm_a");
        
    public FileSystem(EntryPoint app){
        forApp = app;
        //drivers = new WebDriverLoader(app);
        //driverPaths = new HashMap<>();
    }
    
    public void init() throws IOException{
        createAbsentFolders();
    }
    
    /**
     * Returns whether or not the given directory exists.
     * 
     * @param dirPath the full path to the directory to check
     * @return whether or not the given directory exists.
     */
    private boolean dirExists(String dirPath){
        Path p = Paths.get(dirPath);
        return Files.exists(p) && Files.isDirectory(p);
    }
    
    /**
     * Creates the given directory, if it does not yet exist.
     * 
     * @param dirPath the full filepath to the directory you wish to create.
     * @throws IOException if the directory does not yet exist and cannot be created.
     */
    public void createIfAbsent(String dirPath) throws IOException{
        if(!dirExists(dirPath)){
            forApp.getLog().log("Creating directory " + dirPath);
            Files.createDirectory(Paths.get(dirPath));
        }
    }
    
    /**
     * Creates the directories that this program needs
     * that haven't been created yet.
     * 
     * @throws IOException if any of the directories cannot be created.
     */
    private void createAbsentFolders() throws IOException{
        createIfAbsent(ARCDH_FOLDER_PATH);
        createIfAbsent(APP_FOLDER_PATH);
        createIfAbsent(LOG_FOLDER_PATH);
        createIfAbsent(JAR_FOLDER_PATH);
    }
    
    /**
     * Used to create files, usually in the application resource folder.
     * 
     * @param parentFolder the folder to save the new file to
     * @param fileName the name of the file. This should include the file extension, but not the entire file path
     * @param contents the text contents to write to the newly created file.
     * @throws IOException if any mishaps occur when creating or writing to the new file.
     */
    private void saveToFile(String parentFolder, String fileName, String contents) throws IOException{
        createIfAbsent(parentFolder);
        File newFile = new File(parentFolder + File.separator + fileName);
        FileWriterUtil.writeToFile(newFile, contents);
    }
    
    public void saveLog(Logger log) throws IOException{
        saveToFile(LOG_FOLDER_PATH, "Log" + LocalDateTime.now().format(DATE_FORMAT) + ".txt", log.getLog());
    }
}
