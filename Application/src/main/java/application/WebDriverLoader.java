package application;

import io.FileSystem;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import util.Browser;

/**
 * Use this to move stuff out of FileSystem
 * @author Matt
 */
public final class WebDriverLoader {
    public static final String DRIVER_FOLDER_PATH = FileSystem.APP_FOLDER_PATH + File.separator + "webdrivers";
    private final Application forApp;
    private final HashMap<Browser, String> driverPaths;
    
    public WebDriverLoader(Application app){
        forApp = app;
        driverPaths = new HashMap<>();
    }
    
    /**
     * Loads all WebDriver executables from
     * the given directory.
     * 
     * @param dirPath the directory containing WebDriver executables
     */
    public void loadFolder(String dirPath){
        boolean debug = false;
        if(debug){
            System.out.println("Contents of " + dirPath);
        }
        File driverFolder = new File(dirPath);
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
                saveDriverPath(b, savedDriver.getAbsolutePath());
            }
        }
        if(debug){
            System.out.println("End of driver folder.");
        }
    }
    
    public void loadSavedWebDrivers(){
        loadFolder(DRIVER_FOLDER_PATH);
    }
    
    /**
     * Saves the path to a webdriver executable so that the
     * program can launch the WebDriver.
     * 
     * @param forBrowser the browser driven by the executable
     * @param path the complete file path to the executable
     */
    private void saveDriverPath(Browser forBrowser, String path){
        driverPaths.put(forBrowser, path);
        System.setProperty(forBrowser.getDriverEnvVar(), path);
    }
    
    /**
     * De-sets the path to the webdriver executable
     * of the given browser. If the webdriver is currently
     * saved to the program's resource folder, deletes it.
     * 
     * @param forBrowser the browser whose webdriver 
     * should have its path removed.
     */
    public void clearDriverPath(Browser forBrowser){
        String path = driverPaths.get(forBrowser);
        if(path != null){
            driverPaths.remove(forBrowser);
            System.clearProperty(forBrowser.getDriverEnvVar());
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
            saveDriverPath(b, newPath);
        } catch (IOException ex) {
            forApp.getLog().logError(ex);
            saveDriverPath(b, path);
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
    
    public void init() throws IOException{
        this.forApp.getResources().createIfAbsent(DRIVER_FOLDER_PATH);
        loadSavedWebDrivers();
    }
}
