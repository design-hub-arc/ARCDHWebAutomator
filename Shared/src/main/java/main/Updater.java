package main;

import io.FileReaderUtil;
import io.FileSystem;
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
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
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
    private final GitHubUrl jarDownloadUrl;
    private final String jarLocalPath;
    
    //                                                   single quotes for literal
    private static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    // this is the format the GitHub API outputs dates in
    
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
    
    /**
     * 
     * @param jarUrl the URL pointing to the JAR file this should download if it needs to update.
     * @param localPath the complete path to the file where this should download the JAR file to.
     */
    public Updater(GitHubUrl jarUrl, String localPath){
        jarDownloadUrl = jarUrl;
        jarLocalPath = localPath;
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
    public Date getInstalledJarDate(){
        Date date = null;
        if(isInstalled()){
            try {
                File f = new File(jarLocalPath);
                FileTime ft = Files.getLastModifiedTime(Paths.get(f.getAbsolutePath()));
                date = FORMAT.parse(FORMAT.format(ft.toMillis()));
                System.out.println("installed JAR last updated: " + FORMAT.format(date));
            } catch (IOException ex) {
                Logger.logError("Updater.getInstalledJarDate", ex);
            } catch (ParseException ex) {
                Logger.logError("Updater.getInstalledJarDate", ex);
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
     * @return the last JAR compilation date, or null if an error occurs
     */
    public Date getLatestManifestDate(){
        Date date = null;
        
        //https://developer.github.com/v3/
        //https://developer.github.com/v3/repos/commits/
        Logger.log("Updater.getLatestManifestDate", "Checking GitHub API for latest update....");
        try {
            URL apiUrl = new URL(String.format("https://api.github.com/repos/%s/%s/commits?sha=%s&path=%s&page=1&per_page=1", jarDownloadUrl.getOwner(), jarDownloadUrl.getRepo(), jarDownloadUrl.getBranch(), jarDownloadUrl.getFilePath()));
            JsonReader read = Json.createReader(apiUrl.openStream());
            JsonArray arr = read.readArray();
            read.close();
            //System.out.println(arr);
            String sDate = arr.get(0).asJsonObject().getJsonObject("commit").getJsonObject("author").getString("date");
            //System.out.println(sDate);
            date = FORMAT.parse(sDate);
            Logger.log("Updater.getLatestManifestDate", "GitHub: " + FORMAT.format(date));
        } catch (MalformedURLException ex) {
            Logger.logError("Updater.getLatestManifestDate", ex);
        } catch (IOException ex) {
            Logger.logError("Updater.getLatestManifestDate", ex);
        } catch (ParseException ex) {
            Logger.logError("Updater.getLatestManifestDate", ex);
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
        Logger.log("Updater.downloadAndInstall", "Downloading...");
        BufferedInputStream buff = new BufferedInputStream(downloadMe.openStream());
        FileOutputStream out = new FileOutputStream(writeToMe);
        ReadableByteChannel in = Channels.newChannel(buff);
        Logger.log("Updater.downloadAndInstall", "Installing...");
        out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
        Logger.log("Updater.downloadAndInstall", "Installed successfully!");
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
    private boolean latestIsNewer(Date currVersion, Date latestVersion){
        boolean latestIsNewer = false;
        
        if(currVersion == null && latestVersion == null){
            //cannot compare
            Logger.logError("Updater.latestIsNewer", "both the current and latest JAR version are null, so I cannot compare them");
        } else if(latestVersion == null){
            //latest is null, current isn't
            Logger.logError("Updater.latestIsNewer", "Something may be wrong with the file on GitHub: the current version is dated " + FORMAT.format(currVersion) + ", while the GitHub manifest lists null");
        } else if(currVersion == null){
            //current is null, latest isn't
            latestIsNewer = true;
        } else {
            //neither is null, so compare
            Logger.log("Updater.latestIsNewer", "Currently installed is " + FORMAT.format(currVersion) + ", " + "newest is " + FORMAT.format(latestVersion));
            if(latestVersion.after(currVersion)){
                Logger.log("Updater.latestIsNewer", "Currently installed app is outdated, please wait while I install the newest version...");
                latestIsNewer = true;
            } else {
                Logger.log("Updater.latestIsNewer", "Looks like everything is up to date!");
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
            Date currentlyInstalled = getInstalledJarDate();
            Date latestVersion = getLatestManifestDate();
            shouldInstall = latestIsNewer(currentlyInstalled, latestVersion);
        } else {
            //TODO: if this is running from JAR, move it to the app bin folder
            Logger.log("Updater.shouldInstall", jarLocalPath + " is not installed, so I will install it.");
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
    
    @Override
    public String toString(){
        return String.format("Updater:\n * Jar: %s\n * Install to %s", jarDownloadUrl.toString(), jarLocalPath);
    }
    
    /**
     * Updates all JAR files specified
     * in jarInfo.csv file that are not currently
     * being run. The reposityInfo.properties file
     * specifies the repository to check. Both of
     * these files are located under Shared/Resources
     * @param exclude the entire paths of local JAR files to exclude,
     * such as the running JAR file.
     * @throws java.io.IOException
     */
    public static void updateAll(String[] exclude) throws IOException{
        
        Properties repoProps;
        // first, read repository file
        try (InputStream in = Updater.class.getResourceAsStream("/repositoryInfo.properties")) {
            repoProps = new Properties();
            repoProps.load(in);
        }
        String repoOwner = repoProps.getProperty("owner");
        String repoName = repoProps.getProperty("repository-name");
        String repoBranch = repoProps.getProperty("branch");
        
        // next, get get the JAR file information
        String content = FileReaderUtil.readStream(Updater.class.getResourceAsStream("/jarInfo.txt"));
        String[] rows = content.split(System.lineSeparator()); 
        
        ArrayList<Updater> updaters = new ArrayList<>();
        Updater up;
        GitHubUrl url;
        
        for (String row : rows) {
            url = new GitHubUrl(repoOwner, repoName, repoBranch, row.trim());
            up = new Updater(
                url,
                FileSystem.JAR_FOLDER_PATH + File.separator + url.getFileName()
            );
            updaters.add(up);
        }
        
        // now, download and install
        updaters.forEach((updater)->{
            boolean excludeMe = false;
            Logger.log("Updater.updateAll", updater.toString());
            for(String excluded : exclude){
                if(excluded.equals(updater.jarLocalPath)){
                    excludeMe = true;
                }
            }
            if(excludeMe){
                Logger.log("Updater.updateAll", "Don't update " + updater.jarLocalPath);
            } else {
                try {
                    updater.run();
                } catch (IOException ex) {
                    Logger.logError("Updater.updateAll", ex);
                }
            }
        });
    }
    
    public static void updateAll() throws IOException{
        updateAll(new String[]{});
    }
    
    public static void main(String[] args) throws IOException{
        Updater.updateAll();
        System.out.println(Logger.getLog());
    }
}
