package application;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    //https://api.github.com/repos/design-hub-arc/ARCDHWebAutomator/git/refs
    private static final String DOWNLOAD_URL = "https://api.github.com/repos/design-hub-arc/ARCDHWebAutomator/contents/build/libs/ARCDHWebAutomator.jar";
    
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
        }
        
        //https://stackoverflow.com/questions/482560/can-you-tell-on-runtime-if-youre-running-java-from-within-a-jar
        URL thisUrl = Updater.class.getResource("Updater.class");
        System.out.println("URL is " + thisUrl);
        String prefix = thisUrl.toString().split(":")[0];
        if("jar".equals(prefix)){
            System.out.println("Running from JAR");
            String jarDate = getJarCompileDate();
            if(jarDate == null){
                System.err.println("Failed to get JAR compile date");
            } else {
                System.out.println("JAR was compiled on " + jarDate);
                String gitHubDate = getLatestCompileDate();
            }
        } else {
            System.out.println("Not running from JAR");
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
        
        //change this to read the JAR folder instead
        URL manifestUrl = Updater.class.getResource("/META-INF/MANIFEST.MF");
        if(manifestUrl == null){
            System.err.println("Failed to load /META-INF/MANIFEST.MF");
        } else {
            try {
                JarURLConnection conn =(JarURLConnection)manifestUrl.openConnection();
                /*
                conn.getMainAttributes().entrySet().forEach((kv)->{
                    System.out.println(kv.getKey() + ", " + kv.getValue());
                });
                */
                String jarDate = conn.getMainAttributes().getValue("Date");
                
                if(jarDate == null){
                    System.err.println("/META-INF/MANIFEST.MF does not contain value 'Date'");
                } else {
                    ret = jarDate;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
            System.out.println("Data is " + gitHubPage);
            System.out.println(gitHubPage.body().text());
            JsonObject asJson = Json
                .createReader(
                    new StringReader(
                        gitHubPage
                            .body()
                            .text()
                    )
                ).readObject();
            System.out.println("JSON content: " + asJson.getString("content"));
            String properlyEncoded = new String(Base64.getEncoder().encode("Manifest-Version: 1.0\nMain-Class: application.Application\n".getBytes()));
            System.out.println("Should be : " + properlyEncoded);
            System.out.println("Properly decoded: " + new String(Base64.getDecoder().decode(properlyEncoded)));
            String decoded = new String(Base64.getDecoder().decode(asJson.getString("content").replaceAll("\n", "")));
            System.out.println("decoded: " + decoded);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ret;
    }
    
    public void install() throws IOException{
        Document download = Jsoup
            .connect(DOWNLOAD_URL)
            .ignoreContentType(true)
            .get();
        System.out.println(download.toString());
    }
    
    public static void main(String[] args) throws IOException{
        Application.getInstance().getResources().init();
        //new Updater().getJarCompileDate();
        new Updater().getLatestCompileDate();
        //new Updater().checkForUpdates();
    }
}
