package launcher;

import application.Application;
import application.ApplicationResources;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.jar.JarFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import javax.json.Json;
import javax.json.JsonObject;
import logging.ErrorLogger;
import logging.Logger;

/**
 * The Updater class is used to check for updates to the program on GitHub,
 * as well as installing the JAR files used by the program.
 * 
 * @author Matt Crow
 */
public class Updater {
    private final ArrayList<Logger> loggers;
    
    private static final String APP_MANIFEST_URL = "https://api.github.com/repos/design-hub-arc/ARCDHWebAutomator/contents/build/tmp/jar/MANIFEST.MF";
    private static final String APP_DOWNLOAD_URL = "https://raw.githubusercontent.com/design-hub-arc/ARCDHWebAutomator/master/build/libs/ARCDHWebAutomator.jar";
    public static final String APP_JAR_PATH = ApplicationResources.JAR_FOLDER_PATH + File.separator + "ARCDHWebAutomator.jar";
    
    public Updater(){
        loggers = new ArrayList<>();
        loggers.add(new Logger() {
            @Override
            public void log(String s) {
                System.out.println(s);
            }

            @Override
            public String getLog() {
                return "";
            }
        });
    }
    
    public void addLogger(Logger l){
        loggers.add(l);
    }
    
    private void writeOutput(String msg){
        loggers.forEach((logger)->logger.log(msg));
    }
    private void reportError(String msg){
        loggers.stream().filter((logger)->{
            return logger instanceof ErrorLogger;
        }).map((logger)->{
            return (ErrorLogger)logger;
        }).forEach((logger)->{
            logger.logError(msg);
        });
    }
    private void reportError(Exception ex){
        loggers.stream().filter((logger)->{
            return logger instanceof ErrorLogger;
        }).map((logger)->{
            return (ErrorLogger)logger;
        }).forEach((logger)->{
            logger.logError(ex);
        });
    }
    
    public void runChecks(){        
        try {
            Application.getInstance().getResources().init();
        } catch (IOException ex) {
            reportError("Failed to initialize application resources");
            reportError(ex);
        }
        
        writeOutput("Running Updater.runChecks()...");
        
        boolean isInstalled = appIsInstalled();
        writeOutput(String.format("Main application %s installed", (isInstalled) ? "is" : "is not"));
        if(isInstalled){
            writeOutput("Application is installed, checking when it was last updated...");
            String currentlyInstalledDate = getInstalledAppJarDate();
            String mostRecentUpdate = getMostRecentAppUpdate();
            
            if(currentlyInstalledDate == null || mostRecentUpdate == null){
                reportError("Cannot compare dates, as at least one is null.");
            } else {
                //compare dates
                DateFormat format = new SimpleDateFormat("dd-M-yyyy");
                try {
                    Date currDate = format.parse(currentlyInstalledDate);
                    Date newestDate = format.parse(mostRecentUpdate);
                    if(newestDate.after(currDate)){
                        writeOutput("Currently installed app is outdated, please wait while I install the newest version...");
                        installApp();
                    } else {
                        writeOutput("Looks like everything is up to date!");
                    }
                } catch (ParseException ex) {
                    reportError(ex);
                } catch (IOException ex) {
                    reportError("Failed to to update");
                    reportError(ex);
                }
            }
        } else {
            writeOutput("Please wait while I download and install the application...");
            try{
                installApp();
                writeOutput("Installation successful!");
            } catch (IOException ex) {
                reportError("Failed to install application. Aborting.");
                reportError(ex);
                System.exit(-1);
            }
        }
    }
    
    /**
     * Checks to see if the main application
     * JAR file is installed in the proper directory.
     * 
     * @return 
     */
    public boolean appIsInstalled(){
        return Files.exists(Paths.get(APP_JAR_PATH));
    }
    
    /**
     * Downloads and installs the main application
     * JAR file to the APP_JAR_PATH file
     * 
     * @throws IOException if the download or install fail 
     */
    private void installApp() throws IOException{
        //https://www.baeldung.com/java-download-file
        URL downloadMe = new URL(APP_DOWNLOAD_URL);
        File writeToMe = new File(APP_JAR_PATH);
        BufferedInputStream buff = new BufferedInputStream(downloadMe.openStream());
        FileOutputStream out = new FileOutputStream(writeToMe);
        ReadableByteChannel in = Channels.newChannel(buff);
        out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
    }
    
    /**
     * Reads the manifest of the currently installed application,
     * and returns when it was last compiled.
     * 
     * @return the compilation date of the JAR file for the main application,
     * or null if none is present.
     */
    private String getInstalledAppJarDate(){
        String ret = null;
        
        if(appIsInstalled()){
            //extract compile date from JAR
            try {
                JarFile jar = new JarFile(APP_JAR_PATH);
                writeOutput("JAR file manifest:");
                jar.getManifest().getMainAttributes().forEach((s, attr)->{
                    writeOutput(String.format("* %s: %s", s, attr.toString()));
                });
                String jarDate = jar.getManifest().getMainAttributes().getValue("Date");
                if(jarDate == null){
                    reportError("JAR manifest does not contain attribute 'Date'");
                }
                ret = jarDate;
            } catch (IOException ex) {
                reportError(ex);
            }
        }
        
        return ret;
    }
    
    /**
     * Returns the most recent compile date for the JAR file
     * on GitHub.
     * 
     * @return 
     */
    private String getMostRecentAppUpdate(){
        String ret = null;
        Document gitHubPage;
        try {
            gitHubPage = Jsoup
                .connect(APP_MANIFEST_URL)
                .ignoreContentType(true)
                //.header("Accept", "application/vnd.github.VERSION.text+json")
                .get();
            JsonObject asJson = Json
                .createReader(
                    new StringReader(
                        gitHubPage
                            .body()
                            .text()
                    )
                ).readObject();
            //                                                                                need this, otherwise it doesn't decode
            String decoded = new String(Base64.getDecoder().decode(asJson.getString("content").replaceAll("\n", "")));
            writeOutput("decoded: " + decoded + "");
        } catch (IOException ex) {
            reportError(ex);
        }
        return ret;
    }
    
    public static void main(String[] args) throws IOException{
        new Updater().runChecks();
    }
}
