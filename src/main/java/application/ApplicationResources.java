package application;

import io.FileSelector;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
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
    
    public void init() throws IOException{
        createAbsentFolders();
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
     * Creates the given directory, if it does not yet exist.
     * 
     * @param dirPath the full filepath to the directory you wish to create.
     * @throws IOException if the directory does not yet exist and cannot be created.
     */
    private void createIfAbsent(String dirPath) throws IOException{
        if(!dirExists(dirPath)){
            System.out.println("Creating directory " + dirPath);
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
        createIfAbsent(companyFolderName);
        createIfAbsent(applicationFolderName);
        createIfAbsent(driverFolderName);
    }
    
    /**
     * Sets the system variable for a webdriver executable, allowing
     * the Selenium WebDriver subclasses to work.
     * 
     * @param b the browser the given webdriver is for
     * @param path the full file path to a webdriver executable
     */
    private void putDriverPath(Browser b, String path){
        driverPaths.put(b, path);
        System.setProperty(b.getDriverEnvVar(), path);
    }
    
    /**
     * Removes the given Browser's driver path from
     * the system properties.
     * This method should be used whenever an invalid driver path is entered.
     * If the webdriver for the given browser is stored in the driver folder,
     * deletes that file.
     * 
     * @param b the browser whose webdriver should have its path cleared from the program's memory.
     */
    public void clearDriverPath(Browser b){
        String path = driverPaths.get(b);
        if(path != null){
            driverPaths.remove(b);
            System.clearProperty(b.getDriverEnvVar());
            if(Paths.get(path).getParent().toString().equals(driverFolderName)){
                new File(path).delete();
            }
        }
    }
    
    /**
     * Removes all driver paths from both this class
     * and the user's environment variables. Also deletes
     * all webdrivers from the drivers folder.
     */
    public void clearAllDriverPaths(){
        driverPaths.forEach((Browser b, String path)->{
            //calling clearDriverPath(browser) would throw a concurrent modification exception
            System.clearProperty(b.getDriverEnvVar());
        });
        driverPaths.clear();
        Path driverFolder = Paths.get(driverFolderName);
        Arrays.stream(driverFolder.toFile().listFiles()).forEach((File f)->{
            f.delete();
        });
    }
    
    /**
     * Copies and stores the given web driver executable to the driver folder
     * if it is not yet present in that folder. This allows the program to
     * more easily remember the location of the executable upon exiting and
     * reloading the program.
     * 
     * @param b the browser associated with the webdriver executable given
     * by path.
     * @param path the full file path to a webdriver executable.
     * @return the full file path leading to the new executable
     * @throws IOException if the file cannot be copied.
     */
    private String copyWebDriver(Browser b, String path) throws IOException{
        if(b == null){
            throw new NullPointerException("Cannot load webdriver if browser is unknown");
        }
        if(path == null){
            throw new NullPointerException("Cannot load webdriver from null path");
        }
        String driverPath = path;
        Path origPath = Paths.get(path);
        System.out.println(origPath.getParent().toString());
        System.out.println(driverFolderName);
        if(!origPath.getParent().toString().equals(driverFolderName)){
            driverPath = driverFolderName + File.separator + origPath.getFileName().toString();
            Files.copy(origPath, Paths.get(driverPath));
        }
        
        return driverPath;
    }
    
    /**
     * Sets the path to a WebDriver executable so that
     * the program can use the WebDriver associated with
     * the given browser.
     * 
     * The program will attempt to copy the driver to its
     * resource folder so that it can "remember" the location
     * of the driver after the user quits the program.
     * 
     * If this copying attempt fails, <b>the program will not crash</b>.
     * Instead, the driver path will simply not be remembered.
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
            putDriverPath(b, newPath);
        } catch (IOException ex) {
            ex.printStackTrace();
            putDriverPath(b, path);
        }
    }
    
    public static void main(String[] args){
        FileSelector.chooseExeFile("choose chrome webdriver", (File f)->{
            getInstance().loadWebDriver(Browser.CHROME, f.getAbsolutePath());
        });
    }
}
