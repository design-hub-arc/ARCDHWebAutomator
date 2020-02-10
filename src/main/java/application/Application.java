package application;

import gui.ApplicationWindow;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.swing.JFrame;
import logging.ApplicationLog;

/**
 * Application serves as the entry point for
 * the program.
 * 
 * @author Matt Crow
 */
public class Application {
    private final ApplicationResources resources;
    private final ApplicationLog log;
    private final WindowAdapter closeListener;
    
    private static Application instance;
    
    private Application(){
        if(instance != null){
            throw new RuntimeException("Cannot instantiate more than one instance of Application. Use Application.getInstance() instead");
        }
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
    
    /**
     * Once the given JFrame closes, this will
     * automatically save its log.
     * 
     * @param w the JFrame to listen to 
     */
    public void listenToWindow(JFrame w){
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
    
    public void start(){
        try {
            getResources().init();
        } catch (IOException ex) {
            getLog().logError(ex);
        }
        ApplicationWindow w = new ApplicationWindow(this); //automatically listens to window
    }
    
    public static void main(String[] args) throws IOException{
        Application app = getInstance();
        app.start();
        
        URL manifest = Application.class.getResource("/META-INF/MANIFEST.MF");
        if(manifest != null){
            try {
                JarURLConnection conn = (JarURLConnection)manifest.openConnection();
                
                System.out.println(conn.getMainAttributes().size());
                conn.getMainAttributes().entrySet().forEach((kv)->{
                    System.out.println(kv.getKey() + ", " + kv.getValue());
                });
                
                Manifest man = conn.getManifest();
                System.out.println(man.getEntries().size() + " entries");
                man.getEntries().forEach((String key, Attributes attrs)->{
                    System.out.println("#" + key + ":");
                    attrs.entrySet().forEach((kv)->{
                        System.out.println(kv.getKey() + ": " + kv.getValue());
                    });
                });
                
                man.getMainAttributes().forEach((k, v)->{
                    System.out.println(k + ", " + v);
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public ApplicationLog getLog(){
        return log;
    }
}
