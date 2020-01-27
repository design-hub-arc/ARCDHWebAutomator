package application;

import gui.ApplicationWindow;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import logging.ErrorLogger;

/**
 * Application serves as the entry point for
 * the program.
 * 
 * @author Matt Crow
 */
public class Application{
    private final ApplicationResources resources;
    private ApplicationWindow window;
    private final ErrorLogger errorLog;
    private final WindowAdapter closeListener;
    
    private static Application instance;
    
    private Application(){
        if(instance != null){
            throw new RuntimeException("Cannot instantiate more than one instance of Application. Use Application.getInstance() instead");
        }
        window = null;
        resources = ApplicationResources.getInstance();
        errorLog = new ErrorLogger();
        
        closeListener = new WindowAdapter(){
            // for some reason, windowClosed doesn't fire.
            @Override
            public void windowClosing(WindowEvent e){
                if(errorLog.hasLoggedError()){
                    writeErrorLog();
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
     * Used to get the error log for the application,
     * where you should report errors.
     * 
     * @return the ErrorLogger used by the application 
     */
    public ErrorLogger getErrorLog(){
        return errorLog;
    }
    
    
    
    
    
    /**
     * Dumps the contents of this'
     * error log to a file in the application
     * resource folder.
     */
    public void writeErrorLog(){
        System.out.println("write error log");
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
}
