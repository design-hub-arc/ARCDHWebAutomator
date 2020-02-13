package application;

import java.io.IOException;
import java.io.StringReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * The Updater class is used to check for updates to the program on GitHub
 * @author Matt Crow
 */
public class Updater {
    public boolean checkForUpdates(){
        boolean shouldUpdate = false;
        
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
    
    private String getJarCompileDate(){
        String ret = null;
        
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
                .connect("https://api.github.com/repos/design-hub-arc/ARCDHWebAutomator/contents/build/tmp/jar/MANIFEST.MF")
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
    
    public static void main(String[] args){
        new Updater().getLatestCompileDate();
        //new Updater().checkForUpdates();
    }
}
