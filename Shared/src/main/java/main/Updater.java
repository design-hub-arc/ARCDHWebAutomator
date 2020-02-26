package main;

import io.GitHubUrl;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
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
    private final GitHubUrl manifestUrl;
    private final GitHubUrl jarDownloadUrl;
    private final String jarLocalPath;
    private final ArrayList<Logger> loggers;
    
    /**
     * 
     * @param manifestFileUrl the URL pointing to the MANIFEST.MF file for the JAR this should update.
     * @param jarUrl the URL pointing to the JAR file this should download if it needs to update.
     * @param localPath the complete path to the file where this should download the JAR file to.
     */
    public Updater(GitHubUrl manifestFileUrl, GitHubUrl jarUrl, String localPath){
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
        ErrorLogger[] errLogs = loggers.stream()
            .filter((logger)->{
            return logger instanceof ErrorLogger;
        }).map((logger)->{
            return (ErrorLogger)logger;
        }).toArray((size)->new ErrorLogger[size]);
        if(errLogs.length == 0){
            System.err.println(msg);
        } else {
            for(ErrorLogger logger : errLogs){
                logger.logError(msg);
            }
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
        ErrorLogger[] errLogs = loggers.stream()
            .filter((logger)->{
            return logger instanceof ErrorLogger;
        }).map((logger)->{
            return (ErrorLogger)logger;
        }).toArray((size)->new ErrorLogger[size]);
        if(errLogs.length == 0){
            ex.printStackTrace();
        } else {
            for(ErrorLogger logger : errLogs){
                logger.logError(ex);
            }
        }
    }
    
    
    /*
    Local file system methods
    */
    
    /**
     * 
     * @return whether or not the JAR file is installed in the proper directory 
     */
    public boolean isInstalled(){
        return Files.exists(Paths.get(jarLocalPath));
    }
    
    /**
     * Reads the manifest of the currently installed application,
     * and returns when it was last compiled.
     * 
     * @return the compilation date of the JAR file for the main application,
     * or null if none is present.
     */
    public String getInstalledJarDate(){
        String date = null;
        if(isInstalled()){
            try {
                JarFile jar = new JarFile(jarLocalPath);
                writeOutput("Currently installed JAR manifest:");
                jar.getManifest().getMainAttributes().forEach((s, attr)->{
                    writeOutput(String.format("* %s: %s", s, attr.toString()));
                });
                date = jar.getManifest().getMainAttributes().getValue("Date");
                if(date == null){
                    reportError("JAR manifest does not contain attribute 'Date'");
                }
            } catch (IOException ex) {
                reportError(ex);
            }
        }
        return date;
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
            URL manUrl = new URL(manifestUrl.toString());
            try (InputStream in = manUrl.openStream()) {
                Manifest mf = new Manifest(in);
                writeOutput("GitHub Manifest:");
                mf.getMainAttributes().forEach((a, b)->{
                    writeOutput(String.format("* %s: %s", a, b));
                });
                if(mf.getMainAttributes().getValue("Date") == null){
                    reportError("Manifest does not contain attribute 'Date'");
                } else {
                    date = mf.getMainAttributes().getValue("Date");
                }
            }
        } catch (MalformedURLException ex) {
            reportError("Malformed URL in " + getClass().getName() + ": " + manifestUrl);
        } catch (IOException ex) {
            reportError(ex);
        }
        return date;
    }
    
    /**
     * Downloads the JAR file from GitHub,
     * and installs it in this.jarLocalPath
     * @throws java.io.IOException if anything fails while either downloading or installing
     */
    public void downloadAndInstall() throws IOException{
        //https://www.baeldung.com/java-download-file
        URL downloadMe = new URL(jarDownloadUrl.toString());
        File writeToMe = new File(jarLocalPath);
        writeOutput("Downloading...");
        BufferedInputStream buff = new BufferedInputStream(downloadMe.openStream());
        FileOutputStream out = new FileOutputStream(writeToMe);
        ReadableByteChannel in = Channels.newChannel(buff);
        writeOutput("Installing...");
        out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
        writeOutput("Installed successfully!");
    }
    
    
    /*
    Process methods
    */
    
    /**
     * Checks to see if the latest version is newer
     * than the current version. If current version is
     * null, but latest is not, latest is considered newer.
     * 
     * @param currVersion
     * @param latestVersion
     * @return 
     */
    private boolean latestIsNewer(String currVersion, String latestVersion){
        boolean latestIsNewer = false;
        
        if(currVersion == null && latestVersion == null){
            //cannot compare
            reportError("both the current and latest JAR version are null, so I cannot compare them");
        } else if(latestVersion == null){
            //latest is null, current isn't
            reportError("Something may be wrong with the file on GitHub: the current version is dated " + currVersion + ", while the GitHub manifest lists null");
        } else if(currVersion == null){
            //current is null, latest isn't
            latestIsNewer = true;
        } else {
            //neither is null, so compare
            DateFormat format = new SimpleDateFormat("dd-M-yyyy");
            try {
                Date currDate = format.parse(currVersion);
                Date newestDate = format.parse(latestVersion);
                if(newestDate.after(currDate)){
                    writeOutput("Currently installed app is outdated, please wait while I install the newest version...");
                    latestIsNewer = true;
                } else {
                    writeOutput("Looks like everything is up to date!");
                }
            } catch (ParseException ex) {
                reportError(ex);
            }
        }
        
        return latestIsNewer;
    }
    
    /**
     * 
     * @return whether or not this should
     * attempt to install a new version of
     * the application
     */
    public boolean shouldInstall(){
        boolean shouldInstall = false;
        
        if(isInstalled()){
            String currentlyInstalled = getInstalledJarDate();
            String latestVersion = getLatestManifestDate();
            shouldInstall = latestIsNewer(currentlyInstalled, latestVersion);
        } else {
            //TODO: if this is running from JAR, move it to the app bin folder
            writeOutput(jarLocalPath + " is not installed, so I will install it.");
            shouldInstall = true;
        }
        
        return shouldInstall;
    }
    
    /**
     * Checks for updates, and installs them
     * if they exist.
     * 
     * @throws IOException should the program encounter any errors while installing.
     */
    public void run() throws IOException{
        if(shouldInstall()){
            downloadAndInstall();
        }
    }
}
