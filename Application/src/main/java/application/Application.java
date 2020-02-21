
package application;

import gui.ApplicationWindow;
import io.FileSystem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import launcher.Launcher;
import main.Updater;
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
        updater = new Updater(
            "https://raw.githubusercontent.com/design-hub-arc/ARCDHWebAutomator/master/build/tmp/jar/MANIFEST.MF",
            "https://raw.githubusercontent.com/design-hub-arc/ARCDHWebAutomator/master/build/libs/ARCDHWebAutomator.jar",
            FileSystem.JAR_FOLDER_PATH + File.separator + "ARCDHWebAutomator.jar"
        );
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
    
    private void runLauncher() throws IOException{
        Launcher.getInstance().launch();
    }
    
    public static void main(String[] args) throws IOException{
        Application app = getInstance();
        //app.runLauncher();
        app.start();
        //System.out.println(Application.class.getResource("Application.class"));
        //System.out.println(getInstance().getRunningJar());
    }
}
