package application;

import gui.ApplicationWindow;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import logging.ErrorLogger;
import logging.Logger;

/**
 * Application serves as the entry point for
 * the program.
 * 
 * @author Matt Crow
 */
public class Application implements Logger{
    private final ApplicationResources resources;
    private ApplicationWindow window;
    private final StringBuilder log;
    private final ErrorLogger errorLog;
    private final WindowAdapter closeListener;
    
    private static Application instance;
    
    private Application(){
        if(instance != null){
            throw new RuntimeException("Cannot instantiate more than one instance of Application. Use Application.getInstance() instead");
        }
        window = null;
        resources = ApplicationResources.getInstance();
        log = new StringBuilder();
        errorLog = new ErrorLogger();
        
        closeListener = new WindowAdapter(){
            // for some reason, windowClosed doesn't fire.
            @Override
            public void windowClosing(WindowEvent e){
                if(log.length() >= 0){
                    writeLog();
                }
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
     * Saves the log of this application
     * to the application resource folder.
     */
    public void writeLog(){
        try {
            resources.saveLog(this);
        } catch (IOException ex) {
            System.err.println("Unable to write application log:");
            ex.printStackTrace();
        }
    }
    
    /**
     * Dumps the contents of this'
     * error log to a file in the application
     * resource folder.
     */
    public void writeErrorLog(){
        try {
            resources.saveErrorLog(errorLog);
        } catch (IOException ex) {
            System.err.println("Unable to write error log:");
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

    @Override
    public void log(String s) {
        log.append(s);
    }

    @Override
    public String getLog() {
        return log.toString();
    }
}
