package application;

import gui.ApplicationWindow;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import logging.ApplicationLog;

/**
 * Application serves as the entry point for
 * the program.
 * 
 * @author Matt Crow
 */
public class Application {
    private final ApplicationResources resources;
    private ApplicationWindow window;
    private final ApplicationLog log;
    private final WindowAdapter closeListener;
    
    private static Application instance;
    
    private Application(){
        if(instance != null){
            throw new RuntimeException("Cannot instantiate more than one instance of Application. Use Application.getInstance() instead");
        }
        window = null;
        resources = new ApplicationResources(this);
        log = new ApplicationLog();
        
        closeListener = new WindowAdapter(){
            // for some reason, windowClosed doesn't fire.
            @Override
            public void windowClosing(WindowEvent e){
                if(log.getLog().length() >= 0){
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
    
    public void setWindow(ApplicationWindow w){
        if(window != null){
            // stop listening to old window
            window.removeWindowListener(closeListener);
        }
        window = w;
        w.addWindowListener(closeListener);
    }
    
    public ApplicationResources getResources(){
        return resources;
    }
    
    
    /**
     * Saves the log of this application
     * to the application resource folder.
     */
    public void writeLog(){
        try {
            resources.saveLog(log);
        } catch (IOException ex) {
            System.err.println("Unable to write application log:");
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        Application app = getInstance();
        try {
            app.getResources().init();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ApplicationWindow w = new ApplicationWindow(app);
        app.setWindow(w);
    }
    
    public ApplicationLog getLog(){
        return log;
    }
}
