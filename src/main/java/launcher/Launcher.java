package launcher;

import logging.ApplicationLog;

/**
 * The Launcher is analogous to the Application class,
 * handling updating and starting the application.\
 * 
 * @author Matt Crow
 */
public class Launcher {
    private final ApplicationLog log;
    
    private static Launcher instance;
    
    private Launcher(){
        if(instance != null){
            throw new RuntimeException("Cannot instantiate multiple launchers, use getInstance() instead.");
        }
        log = new ApplicationLog();
    }
    
    public Launcher getInstance(){
        if(instance == null){
            instance = new Launcher();
        }
        return instance;
    }
    
    public void launch(){
        LauncherFrame window = new LauncherFrame();
    }
}
