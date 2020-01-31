package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import util.Browser;

/**
 * Use this to move stuff out of ApplicationResources
 * @author Matt
 */
public final class WebDriverLoader {
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
            if(Paths.get(path).getParent().toString().equals(ApplicationResources.DRIVER_FOLDER_PATH)){
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
}
