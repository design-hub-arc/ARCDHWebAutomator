
package application;

import gui.ApplicationWindow;
import java.io.IOException;
import logging.Logger;
import main.EntryPoint;

/**
 * Application serves as the entry point for
 * the automation program. The main method of\
 * this class starts and launches the GUI where the used can
 * choose which automation to run.
 * 
 * @author Matt Crow
 */
public class Application extends EntryPoint{
    private final WebDriverLoader webDriverLoader;
    private static Application instance;
    
    private Application(){
        super();
        if(instance != null){
            throw new RuntimeException("Cannot instantiate more than one instance of Application. Use Application.getInstance() instead");
        }
        webDriverLoader = new WebDriverLoader(this);
    }
    
    public static Application getInstance(){
        if(instance == null){
            instance = new Application();
        }
        return instance;
    }
    
    public WebDriverLoader getWebDriverLoader(){
        return webDriverLoader;
    }
    
    @Override
    public void doRun(){
        try {
            webDriverLoader.init();
        } catch (IOException ex) {
            Logger.logError("Application.doRun", ex);
        }
        ApplicationWindow w = new ApplicationWindow(this); //automatically listens to window
    }
    
    public static void main(String[] args) throws IOException{
        Application app = getInstance();
        Thread updater = new Thread(){
            @Override
            public void run(){
                app.checkForUpdates();
            }
        };
        updater.start();
        app.run();
    }
}
