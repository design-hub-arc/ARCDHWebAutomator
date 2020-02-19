package application;

import io.FileWriterUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import logging.Logger;
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
    private final Application forApp;
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String ARCDH_FOLDER_PATH = USER_HOME + File.separator + "ARCDH";
    public static final String APP_FOLDER_PATH = ARCDH_FOLDER_PATH + File.separator + "WebAutomator";
    public static final String DRIVER_FOLDER_PATH = APP_FOLDER_PATH + File.separator + "webdrivers";
    public static final String LOG_FOLDER_PATH = APP_FOLDER_PATH + File.separator + "logs";
    public static final String JAR_FOLDER_PATH = APP_FOLDER_PATH + File.separator + "bin";
    
    private final WebDriverLoader drivers;
    //todo: move functionality from this class to WebDriverLoader
    private final HashMap<Browser, String> driverPaths;
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-DD-uuuu_hh_mm_a");
        
    public ApplicationResources(Application app){
        forApp = app;
        drivers = new WebDriverLoader(app);
        driverPaths = new HashMap<>();
    }
    
    public void init() throws IOException{
        createAbsentFolders();
        loadSavedWebDrivers();
    }
    
    private void loadSavedWebDrivers(){
        boolean debug = false;
        File driverFolder = new File(DRIVER_FOLDER_PATH);
        if(debug){
            System.out.println("Contents of driver folder:");
        }
        for(File savedDriver : driverFolder.listFiles()){
            if(debug){
                System.out.println("* " + savedDriver.getName());
            }
            //                                           '.' has special meaning in regex, so we have to interprate it literally
            String[] split = savedDriver.getName().split("\\.");
            String fileName = split[0];
            Browser b = Browser.getByDriverFileName(fileName);
            if(b == null){
                forApp.getLog().logError("Cannot find browser with webdriver named " + fileName);
            } else {
                putDriverPath(b, savedDriver.getAbsolutePath());
            }
        }
        if(debug){
            System.out.println("End of driver folder.");
        }
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
        createIfAbsent(DRIVER_FOLDER_PATH);
        createIfAbsent(LOG_FOLDER_PATH);
        createIfAbsent(JAR_FOLDER_PATH);
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
            if(Paths.get(path).getParent().toString().equals(DRIVER_FOLDER_PATH)){
                try {
                    Files.delete(Paths.get(path));
                } catch (AccessDeniedException ex){
                    forApp.getLog().logError("Unable to delete " + path + ". Please use your task manager to verify that no instances of this executable are being run.");
                    forApp.getLog().logError(ex);
                } catch (IOException ex) {
                    forApp.getLog().logError(ex);
                }
            }
        }
    }
    
    /**
     * Removes all driver paths from both this class
     * and the user's environment variables. Also deletes
     * all webdrivers from the drivers folder
     */
    public void clearAllDriverPaths(){
        driverPaths.forEach((Browser b, String path)->{
            //calling clearDriverPath(browser) would throw a concurrent modification exception
            System.clearProperty(b.getDriverEnvVar());
        });
        driverPaths.clear();
        Path driverFolder = Paths.get(DRIVER_FOLDER_PATH);
        Arrays.stream(driverFolder.toFile().listFiles()).forEach((File f)->{
            try {
                Files.delete(Paths.get(f.getAbsolutePath()));
            } catch (AccessDeniedException ex){
                forApp.getLog().logError("Unable to delete " + f.getAbsolutePath() + ". Please use your task manager to verify that no instances of this executable are being run.");
                forApp.getLog().logError(ex);
            } catch (IOException ex) {
                forApp.getLog().logError(ex);
            }
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
        
        Path origPath = Paths.get(path);
        String driverPath = DRIVER_FOLDER_PATH + File.separator + origPath.getFileName().toString();
        if(!Files.exists(Paths.get(driverPath))){
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
            forApp.getLog().logError(ex);
            putDriverPath(b, path);
        }
    }
    
    /**
     * Returns whether or not the WebDriver for a browser
     * has been loaded by the program.
     * 
     * @param forBrowser the browser to check if there is a WebDriver for.
     * @return 
     */
    public boolean hasWebDriver(Browser forBrowser){
        return driverPaths.containsKey(forBrowser);
    }
    
    /**
     * Returns the file path to the WebDriver for the given browser.
     * @param forBrowser the browser to get the WebDriver path to
     * @return the complete file path to the browser's webdriver, or null if its path isn't set.
     */
    public String getWebDriverPath(Browser forBrowser){
        return driverPaths.get(forBrowser);
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
