package main;

import io.FileSystem;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import logging.ApplicationLog;

/**
 * The EntryPoint class serves as the base
 * for starting points for each subproject.
 * 
 * @author Matt Crow
 */
public class EntryPoint {
    private final ApplicationLog log;
    private final FileSystem resources;
    
    public EntryPoint(){
        log = new ApplicationLog();
        resources = new FileSystem();
    }
    
    public ApplicationLog getLog(){
        return log;
    }
    
    public FileSystem getResources(){
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
    
    public boolean isRunningFromJar(){
        return "jar".equals(getClass().getResource(getClass().getSimpleName() + ".class").toString().split(":")[0]);
    }
    
    /**
     * 
     * @return the name of the JAR file running this program, if any
     */
    public String getRunningJar(){
        String ret = null;
        if(isRunningFromJar()){
            try {
                File jarFile = (new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
                //System.out.println(jarFile.getAbsolutePath());
                ret = jarFile.getName();
            } catch (URISyntaxException ex) {
                log.logError(ex);
            }
        }
        return ret;
    }
}
