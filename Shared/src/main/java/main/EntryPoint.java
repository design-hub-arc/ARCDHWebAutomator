package main;

import io.FileSystem;
import java.io.IOException;
import logging.ApplicationLog;

/**
 *
 * @author Matt
 */
public class EntryPoint {
    private final ApplicationLog log;
    private final FileSystem resources;
    
    public EntryPoint(){
        log = new ApplicationLog();
        resources = new FileSystem(this);
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
}
