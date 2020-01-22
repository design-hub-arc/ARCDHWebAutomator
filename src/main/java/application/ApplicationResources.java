package application;

import io.FileSelector;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Browser;

/**
 * The ApplicationResources class is used
 to interface with the folder containing
 resources utilized by the program, such as
 webdrivers, plugins, and more.
 * This provides a single location where the program
 * can find the resources it needs,
 * minimizing the need for users to select files.
 * 
 * @author Matt Crow
 */
public final class ApplicationResources {
    
    private final String userHome = System.getProperty("user.home");
    private final String companyFolderName = userHome + File.separator + "ARCDH";
    private final String applicationFolderName = companyFolderName + File.separator + "WebAutomator";
    private final String driverFolderName = applicationFolderName + File.separator + "webdrivers";
    private final HashMap<Browser, String> driverPaths;
    
    private static ApplicationResources instance;
    
    private ApplicationResources(){
        if(instance != null){
            throw new RuntimeException("Cannot instantiate more than one instance of singleton class, use getInstance() instead of constructor");
        }
        driverPaths = new HashMap<>();
    }
    
    /**
     * Used to access the singleton instance of this class.
     * If the class has not yet been initialized, instantiates before returning.
     * 
     * @return the instance of this class
     */
    public static ApplicationResources getInstance(){
        if(instance == null){
            instance = new ApplicationResources();
        }
        return instance;
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
     * Creates the directories that this program needs
     * that haven't been created yet.
     * 
     * @throws IOException if any of the directories cannot be created.
     */
    private void createFolders() throws IOException{
        if(!dirExists(companyFolderName)){
            System.out.println("Creating ARCDH folder at " + companyFolderName);
            Files.createDirectory(Paths.get(companyFolderName));
        }
        if(!dirExists(applicationFolderName)){
            System.out.println("Creating folder at " + applicationFolderName);
            Files.createDirectory(Paths.get(applicationFolderName));
        }
        if(!dirExists(driverFolderName)){
            Files.createDirectory(Paths.get(driverFolderName));
        }
    }
    
    private String copyWebDriver(Browser b, String path) throws IOException{
        if(b == null){
            throw new NullPointerException("Cannot load webdriver if browser is unknown");
        }
        if(path == null){
            throw new NullPointerException("Cannot load webdriver from null path");
        }
        
        if(!dirExists(driverFolderName)){
            createFolders();
        }
        
        Path origPath = Paths.get(path);
        String newPath = driverFolderName + File.separator + origPath.getFileName().toString();
        Files.copy(origPath, Paths.get(newPath));
        
        return newPath;
    }
    
    /**
     * Copies a WebDriver executable to this folder so
     * it can be retrieved without user input in the future.
     * 
     * If that fails, the webdriver will have to be selected by
     * the user again the next time they use the program.
     * 
     * @param b the Browser the given WebDriver is for
     * @param path the path to a WebDriver
     */
    public void loadWebDriver(Browser b, String path){
        if(b == null){
            throw new NullPointerException("Cannot load webdriver if browser is unknown");
        }
        if(path == null){
            throw new NullPointerException("Cannot load webdriver from null path");
        }
        Path p = Paths.get(path);
        if(!(Files.exists(p) && !Files.isDirectory(p))){
            throw new IllegalArgumentException("Path must be a path to a valid file, not a directory");
        }
        
        try{
            String newPath = copyWebDriver(b, path);
            driverPaths.put(b, newPath);
        } catch (IOException ex) {
            ex.printStackTrace();
            driverPaths.put(b, path);
        }
    }   
    
    public static void main(String[] args){
        FileSelector.chooseExeFile("choose chrome webdriver", (File f)->{
            getInstance().loadWebDriver(Browser.CHROME, f.getAbsolutePath());
        });
    }
}
