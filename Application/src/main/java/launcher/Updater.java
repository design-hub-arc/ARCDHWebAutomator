package launcher;

import application.Application;
import io.FileSystem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.JarFile;

/**
 * The Updater class is used to check for updates to the program on GitHub,
 * as well as installing the JAR files used by the program.
 * 
 * @author Matt Crow
 */
public class Updater extends main.Updater{
    public static final String APP_JAR_PATH = FileSystem.JAR_FOLDER_PATH + File.separator + "ARCDHWebAutomator.jar";
    
    public Updater(){
        super(
            "https://raw.githubusercontent.com/design-hub-arc/ARCDHWebAutomator/master/build/tmp/jar/MANIFEST.MF",
            "https://raw.githubusercontent.com/design-hub-arc/ARCDHWebAutomator/master/build/libs/ARCDHWebAutomator.jar",
            FileSystem.JAR_FOLDER_PATH + File.separator + "ARCDHWebAutomator.jar"
        );
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
            String mostRecentUpdate = getLatestManifestDate();
            
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
                        downloadAndInstall();
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
                downloadAndInstall();
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
    
    public static void main(String[] args) throws IOException{
        new Updater().getLatestManifestDate();//.runChecks();
    }
}
