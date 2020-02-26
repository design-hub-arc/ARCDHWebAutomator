package main;

import io.FileSystem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import javax.swing.JFrame;
import logging.ApplicationLog;

/**
 * The EntryPoint class serves as the base
 * for starting points for each subproject.
 * This class provides a common template for
 * providing access to logging and file access.
 * 
 * @author Matt Crow
 */
public abstract class EntryPoint {
    private final ApplicationLog log;
    private final FileSystem resources;
    private final WindowAdapter closeListener;
    
    public EntryPoint(){
        log = new ApplicationLog();
        resources = new FileSystem();
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
    
    /**
     * Used to get the message log used
     * by this program. Developers should
     * use this log to record program output
     * which may be useful for debugging the
     * program.
     * 
     * @return this program's output log. 
     */
    public ApplicationLog getLog(){
        return log;
    }
    
    /**
     * used to get the file system interface
     * used by this program. Use this method
     * when the program needs to access files.
     * 
     * @return the file system interface used by this program.
     */
    public FileSystem getResources(){
        return resources;
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
    
    /**
     * Saves the log of this program
     * to the program resource folder.
     */
    public void writeLog(){
        try {
            resources.saveLog(log);
        } catch (IOException ex) {
            System.err.println("Unable to write application log:");
            ex.printStackTrace();
        }
    }
    
    /**
     * 
     * @return whether or not this class was loaded from a JAR file.
     */
    public final boolean isRunningFromJar(){
        return "jar".equals(getClass().getResource(getClass().getSimpleName() + ".class").toString().split(":")[0]);
    }
    
    /**
     * 
     * @return the file path of the JAR file running this program, if any
     */
    public final String getRunningJar(){
        String ret = null;
        if(isRunningFromJar()){
            try {
                File jarFile = (new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
                //System.out.println(jarFile.getAbsolutePath());
                ret = jarFile.getAbsolutePath();
            } catch (URISyntaxException ex) {
                log.logError(ex);
            }
        }
        return ret;
    }
    
    public final void checkForUpdates(){
        String[] exclude = (isRunningFromJar()) ? new String[]{getRunningJar()} : new String[]{};
        System.out.println(Arrays.toString(exclude));
        try {
            Updater.updateAll(exclude, log);
        } catch (IOException ex) {
            log.logError(ex);
        }
    }
    
    /**
     * Subclasses should call this
     * method to run the program.
     * 
     * Initializes resources,
     * checks for updates,
     * and invokes doRun()
     */
    public final void run(){
        try {
            resources.init();
            
        } catch (IOException ex) {
            log.logError(ex);
        }
        doRun();
    }
    
    /**
     * This method is called immediately after run().
     * Subclasses should override this method to
     * launch windows or otherwise start their routines.
     */
    public abstract void doRun();
}
