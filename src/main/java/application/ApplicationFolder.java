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
 * The ApplicationFolder class is used
 * to interface with the folder containing
 * resources utilized by the program, such as
 * webdrivers, plugins, and more.
 * This provides a single location where the program
 * can find the resources it needs,
 * minimizing the need for users to select files.
 * 
 * @author Matt Crow
 */
public final class ApplicationFolder {
    
    private final String userHome = System.getProperty("user.home");
    private final String companyFolderName = userHome + File.separator + "ARCDH";
    private final String applicationFolderName = companyFolderName + File.separator + "WebAutomator";
    private final String driverFolderName = applicationFolderName + File.separator + "webdrivers";
    private final HashMap<Browser, String> driverPaths;
    
    private static ApplicationFolder instance;
    
    private ApplicationFolder(){
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
    public static ApplicationFolder getInstance(){
        if(instance == null){
            instance = new ApplicationFolder();
        }
        return instance;
    }
    
    public boolean companyFolderExists(){
        Path p = Paths.get(companyFolderName);
        return Files.exists(p) && Files.isDirectory(p);
    }
    
    /**
     * Verifies that the folder used by
     * this program exists.
     * 
     * @return whether or not the folder exists
     */
    public boolean folderExists(){
        Path p = Paths.get(applicationFolderName);
        return Files.exists(p) && Files.isDirectory(p);
    }
    
    public boolean driverFolderExists(){
        Path p = Paths.get(driverFolderName);
        return Files.exists(p) && Files.isDirectory(p);
    }
    
    private void createFolder() throws IOException{
        if(!companyFolderExists()){
            System.out.println("Creating ARCDH folder at " + companyFolderName);
            Files.createDirectory(Paths.get(companyFolderName));
        }
        if(!folderExists()){
            System.out.println("Creating folder at " + applicationFolderName);
            Files.createDirectory(Paths.get(applicationFolderName));
        }
        if(!driverFolderExists()){
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
        
        if(!driverFolderExists()){
            createFolder();
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
