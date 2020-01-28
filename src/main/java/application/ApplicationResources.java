package application;

import io.FileSelector;
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
import logging.ErrorLogger;
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
    
    private final String userHome = System.getProperty("user.home");
    private final String companyFolderName = userHome + File.separator + "ARCDH";
    private final String applicationFolderName = companyFolderName + File.separator + "WebAutomator";
    private final String driverFolderName = applicationFolderName + File.separator + "webdrivers";
    private final String logFolderPath = applicationFolderName + File.separator + "logs";
    
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
        loadSavedWebDrivers();
    }
    
    private void loadSavedWebDrivers(){
        File driverFolder = new File(driverFolderName);
        for(File savedDriver : driverFolder.listFiles()){
            //System.out.println(savedDriver.getName());
            //                                           '.' has special meaning in regex, so we have to interprate it literally
            String[] split = savedDriver.getName().split("\\.");
            String fileName = split[0];
            Browser b = Browser.getByDriverFileName(fileName);
            if(b == null){
                System.err.println("Cannot find browser with webdriver named " + fileName);
            } else {
                putDriverPath(b, savedDriver.getAbsolutePath());
            }
        }
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
        createIfAbsent(logFolderPath);
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
                try {
                    Files.delete(Paths.get(path));
                } catch (AccessDeniedException ex){
                    System.err.println("Unable to delete " + path + ". Please use your taks manager to verify that no instances of this executable are being run.");
                } catch (IOException ex) {
                    ex.printStackTrace();
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
        Path driverFolder = Paths.get(driverFolderName);
        Arrays.stream(driverFolder.toFile().listFiles()).forEach((File f)->{
            System.out.println(f.getAbsolutePath());
            try {
                Files.delete(Paths.get(f.getAbsolutePath()));
            } catch (AccessDeniedException ex){
                System.err.println("Unable to delete " + f.getAbsolutePath() + ". Please use your taks manager to verify that no instances of this executable are being run.");
            } catch (IOException ex) {
                ex.printStackTrace();
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
        String driverPath = driverFolderName + File.separator + origPath.getFileName().toString();
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
            ex.printStackTrace();
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
        saveToFile(logFolderPath, "Log" + LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".txt", log.getLog());
    }
    
    public void saveErrorLog(ErrorLogger errorLog) throws IOException{
        saveToFile(logFolderPath, "ErrorLog" + LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".txt", errorLog.getLog());
    }
    
    public static void main(String[] args){
        FileSelector.chooseExeFile("choose chrome webdriver", (File f)->{
            getInstance().loadWebDriver(Browser.CHROME, f.getAbsolutePath());
        });
    }
}
