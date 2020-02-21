package launcher;

import application.Application;
import io.FileSystem;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        
        boolean isInstalled = isInstalled();
        writeOutput(String.format("Main application %s installed", (isInstalled) ? "is" : "is not"));
        if(isInstalled){
            writeOutput("Application is installed, checking when it was last updated...");
            String currentlyInstalledDate = getInstalledJarDate();
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
    
    
    public static void main(String[] args) throws IOException{
        new Updater().runChecks();
    }
}
