package application;

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
import java.util.Base64;
import java.util.Date;
import java.util.jar.JarFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * The Updater class is used to check for updates to the program on GitHub,
 * as well as installing the JAR files used by the program.
 * 
 * @author Matt Crow
 */
public class Updater {
    private static final String APP_MANIFEST_URL = "https://api.github.com/repos/design-hub-arc/ARCDHWebAutomator/contents/build/tmp/jar/MANIFEST.MF";
    private static final String APP_DOWNLOAD_URL = "https://raw.githubusercontent.com/design-hub-arc/ARCDHWebAutomator/master/build/libs/ARCDHWebAutomator.jar";
    
    private static final String APP_JAR_PATH = ApplicationResources.JAR_FOLDER_PATH + File.separator + "ARCDHWebAutomator.jar";
    
    
    
    public void runChecks(){        
        try {
            Application.getInstance().getResources().init();
        } catch (IOException ex) {
            System.err.println("Failed to initialize application resources");
            ex.printStackTrace();
        }
        
        System.out.println("Running Updater.runChecks()...");
        
        boolean isInstalled = appIsInstalled();
        System.out.printf("Main application %s installed\n", (isInstalled) ? "is" : "is not");
        if(isInstalled){
            System.out.println("Application is installed, checking when it was last updated...");
            String currentlyInstalledDate = getInstalledAppJarDate();
            String mostRecentUpdate = getMostRecentAppUpdate();
            
            if(currentlyInstalledDate == null || mostRecentUpdate == null){
                System.err.println("Cannot compare dates, as at least one is null.");
            } else {
                //compare dates
                DateFormat format = new SimpleDateFormat("dd-M-yyyy");
                try {
                    Date currDate = format.parse(currentlyInstalledDate);
                    Date newestDate = format.parse(mostRecentUpdate);
                    if(newestDate.after(currDate)){
                        System.out.println("Currently installed app is outdated, please wait while I install the newest version...");
                        installApp();
                    } else {
                        System.out.println("Looks like everything is up to date!");
                    }
                } catch (ParseException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    System.err.println("Failed to to update");
                    ex.printStackTrace();
                }
            }
        } else {
            System.out.println("Please wait while I download and install the application...");
            try{
                installApp();
                System.out.println("Installation successful!");
            } catch (IOException ex) {
                System.err.println("Failed to install application. Aborting.");
                ex.printStackTrace();
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
    private boolean appIsInstalled(){
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
                System.out.println("JAR file manifest:");
                jar.getManifest().getMainAttributes().forEach((s, attr)->{
                    System.out.printf("* %s: %s\n", s, attr.toString());
                });
                String jarDate = jar.getManifest().getMainAttributes().getValue("Date");
                if(jarDate == null){
                    System.err.println("JAR manifest does not contain attribute 'Date'");
                }
                ret = jarDate;
            } catch (IOException ex) {
                ex.printStackTrace();
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
            System.out.println("decoded: " + decoded);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ret;
    }
    
    public static void main(String[] args) throws IOException{
        new Updater().runChecks();
    }
}
