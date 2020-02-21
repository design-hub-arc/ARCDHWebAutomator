package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import logging.ErrorLogger;
import logging.Logger;

/**
 * The Updater class is used to automatically download
 * updates from GitHub. Since executable files (such as JAR files)
 * cannot be changed while they are running on most operating systems,
 * a JAR file cannot update itself. Instead, each project updates the
 * other.
 * 
 * @author Matt Crow
 */
public class Updater {
    private final String manifestUrl;
    private final String jarDownloadUrl;
    private final String jarLocalPath;
    private final ArrayList<Logger> loggers;
    
    /**
     * 
     * @param manifestFileUrl the URL pointing to the MANIFEST.MF file for the JAR this should update.
     * @param jarUrl the URL pointing to the JAR file this should download if it needs to update.
     * @param localPath the complete path to the file where this should download the JAR file to.
     */
    public Updater(String manifestFileUrl, String jarUrl, String localPath){
        manifestUrl = manifestFileUrl;
        jarDownloadUrl = jarUrl;
        jarLocalPath = localPath;
        loggers = new ArrayList<>();
    }
    
    
    /*
    Output related methods.
    */
    
    
    /**
     * Registers an object implementing the
     * Logger interface to receive output
     * from this object.
     * 
     * @param l 
     */
    public void addLogger(Logger l){
        loggers.add(l);
    }
    
    /**
     * Prints the given message to each
     * logger added to this object. If no
     * loggers are added, prints the message
     * to System.out.
     * 
     * @param msg 
     */
    public void writeOutput(String msg){
        if(loggers.isEmpty()){
            System.out.println(msg);
        }
        loggers.forEach((logger)->logger.log(msg));
    }
    
    /**
     * Prints the given error message to
     * each logger added to this, so long
     * as they implement the ErrorLogger interface.
     * If no such ErrorLogger is attached, prints
     * to System.err.
     * 
     * @param msg 
     */
    public void reportError(String msg){
        Stream<ErrorLogger> errLogs = loggers.stream()
            .filter((logger)->{
            return logger instanceof ErrorLogger;
        }).map((logger)->{
            return (ErrorLogger)logger;
        });
        if(errLogs.count() == 0){
            System.err.println(msg);
        } else {
            errLogs.forEach((logger)->{
                logger.logError(msg);
            });
        }
    }
    
    /**
     * Prints the stack trace for the given
     * exception to each ErrorLogger added to
     * this. If no such logger has been added,
     * prints the stack trace to standard output.
     * 
     * @param ex 
     */
    public void reportError(Exception ex){
        Stream<ErrorLogger> errLogs = loggers.stream().filter((logger)->{
            return logger instanceof ErrorLogger;
        }).map((logger)->{
            return (ErrorLogger)logger;
        });
        if(errLogs.count() == 0){
            ex.printStackTrace();
        } else {
            errLogs.forEach((logger)->{
                logger.logError(ex);
            });
        }
    }
    
    
    /*
    Internet related methods
    */
    
    
    /**
     * Returns the most recent compilation
     * date for the JAR file on GitHub.
     * 
     * @return the last JAR compilation date, or null if the manifest contains no compilation date
     */
    public String getLatestManifestDate(){
        String date = null;
        writeOutput("Checking latest manifest date...");
        try {
            URL manUrl = new URL(manifestUrl);
            try (InputStream in = manUrl.openStream()) {
                Manifest mf = new Manifest(in);
                writeOutput("Manifest:");
                mf.getMainAttributes().forEach((a, b)->{
                    writeOutput(String.format("%s: %s\n", a, b));
                });
                if(mf.getMainAttributes().containsKey("Date")){
                    date = mf.getMainAttributes().getValue("Date");
                } else {
                    reportError("Manifest does not contain attribute 'Date'");
                }
            }
        } catch (MalformedURLException ex) {
            reportError("Malformed URL in " + getClass().getName() + ": " + manifestUrl);
        } catch (IOException ex) {
            reportError(ex);
        }
        return date;
    }
}
