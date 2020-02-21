package launcher;

import java.io.IOException;
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
    
    public static Launcher getInstance(){
        if(instance == null){
            instance = new Launcher();
        }
        return instance;
    }
    
    public void launch() throws IOException{
        LauncherFrame window = new LauncherFrame();
        Updater updater = new Updater();
        updater.addLogger(log);
        updater.addLogger(window.getContent().getTextDisplay());
        updater.run();
        /*
        if(updater.appIsInstalled()){
            Thread appThread = new Thread(){
                @Override
                public void run(){
                    //todo load and run JAR file
                }
            };
            appThread.start();
        }*/
    }
    
    public static void main(String[] args) throws IOException{
        Launcher.getInstance().launch();
    }
}
