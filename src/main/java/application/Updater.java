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
import java.util.Base64;
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
    private static final String MANIFEST_URL = "https://api.github.com/repos/design-hub-arc/ARCDHWebAutomator/contents/build/tmp/jar/MANIFEST.MF";
    //Need to use github data API to download, as the JAR is rather large: https://developer.github.com/v3/git/
    //https://api.github.com/repos/design-hub-arc/ARCDHWebAutomator/git/refs/heads/master
    //this might work: https://raw.githubusercontent.com/design-hub-arc/ARCDHWebAutomator/master/build/libs/ARCDHWebAutomator.jar
    private static final String DOWNLOAD_URL = "https://raw.githubusercontent.com/design-hub-arc/ARCDHWebAutomator/master/build/libs/ARCDHWebAutomator.jar";
    
    private static final String APP_JAR_PATH = ApplicationResources.JAR_FOLDER_PATH + File.separator + "ARCDHWebAutomator.jar";
    /**
     * Checks to see if the main application should be updated.
     * If the application is running from the IDE, this method
     * will always return false.
     * 
     * @return whether or not a new version of the application JAR file is available on GitHub 
     */
    public boolean checkForUpdates(){
        boolean shouldUpdate = false;
        
        String lastCompiled = getJarCompileDate();
        if(lastCompiled == null){
            try {
                //not installed, so download it
                install();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            String mostRecentVersion = getLatestCompileDate();
            System.out.printf("Which is newer, %s or %s?\n", lastCompiled, mostRecentVersion);
        }
        
        return shouldUpdate;
    }
    
    /**
     * Reads the manifest of the currently installed application,
     * and returns when it was last compiled.
     * 
     * @return the compilation date of the JAR file for the main application,
     * or null if none is present.
     */
    private String getJarCompileDate(){
        String ret = null;
        
        System.out.println("JAR folder:");
        File jarFolder = new File(ApplicationResources.JAR_FOLDER_PATH);
        for(File f : jarFolder.listFiles()){
            System.out.println(f.getName());
        }
        System.out.println("End of JAR folder");
        
        if(Files.exists(Paths.get(APP_JAR_PATH))){
            //extract compile date from JAR
            try {
                JarFile jar = new JarFile(APP_JAR_PATH);
                System.out.println("JAR file manifest:");
                jar.getManifest().getMainAttributes().forEach((s, attr)->{
                    System.out.println(s + ": " + attr);
                });
                String jarDate = jar.getManifest().getMainAttributes().getValue("Date");
                if(jarDate == null){
                    System.err.println("JAR manifest does not contain attribute 'Date'");
                } else {
                    ret = jarDate;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
            ret = "TODO: return JAR date";
        }
        
        return ret;
    }
    
    private String getLatestCompileDate(){
        String ret = null;
        Document gitHubPage;
        try {
            gitHubPage = Jsoup
                .connect(MANIFEST_URL)
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
    
    public void install() throws IOException{
        //https://www.baeldung.com/java-download-file
        URL downloadMe = new URL(DOWNLOAD_URL);
        File jarFile = new File(APP_JAR_PATH);
        BufferedInputStream buff = new BufferedInputStream(downloadMe.openStream());
        FileOutputStream out = new FileOutputStream(jarFile);
        ReadableByteChannel in = Channels.newChannel(buff);
        out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
    }
    
    public static void main(String[] args) throws IOException{
        Application.getInstance().getResources().init();
        //new Updater().getJarCompileDate();
        //new Updater().getLatestCompileDate();
        new Updater().checkForUpdates();
    }
}
