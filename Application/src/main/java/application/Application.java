
package application;

import io.FileSystem;
import gui.ApplicationWindow;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import launcher.Launcher;
import launcher.Updater;
import logging.ApplicationLog;
import main.EntryPoint;

/**
 * Application serves as the entry point for
 * the program.
 * 
 * @author Matt Crow
 */
public class Application extends EntryPoint{
    private final Updater updater;
    private final WindowAdapter closeListener;
    private final WebDriverLoader webDriverLoader;
    
    private static Application instance;
    
    private Application(){
        super();
        if(instance != null){
            throw new RuntimeException("Cannot instantiate more than one instance of Application. Use Application.getInstance() instead");
        }
        updater = new Updater();
        webDriverLoader = new WebDriverLoader(this);
        
        closeListener = new WindowAdapter(){
            // for some reason, windowClosed doesn't fire.
            @Override
            public void windowClosing(WindowEvent e){
                if(getLog().getLog().length() >= 0){
                    writeLog();
                }
            }
        };
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
    
    /**
     * Once the given JFrame closes, this will
     * automatically save its log.
     * 
     * @param w the JFrame to listen to 
     */
    public void listenToWindow(JFrame w){
        w.addWindowListener(closeListener);
    }
    
    public void start(){
        try {
            getResources().init();
            webDriverLoader.init();
        } catch (IOException ex) {
            getLog().logError(ex);
        }
        ApplicationWindow w = new ApplicationWindow(this); //automatically listens to window
    }
    
    private void runLauncher(){
        Launcher.getInstance().launch();
    }
    
    public static void main(String[] args) throws IOException{
        Application app = getInstance();
        //app.runLauncher();
        app.start();
    }
}
