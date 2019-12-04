package util;

import io.FileReaderUtil;
import io.FileWriterUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * The User class is used to keep track of
 * user preferences, since I probably won't
 * set permanent system variables (just Google it, it's ugly).
 * 
 * @author Matt Crow
 */
public class User {
    private static final String FILE_NAME = "PSDataImprovementSelenium.user";
    private final HashMap<Browser, String> webDrivers;
    
    public User(){
        webDrivers = new HashMap<>();
    }
    
    /**
     * Sets the system variable for the given browser,
     * then saves the path in this so it can later be
     * saved to a file.
     * 
     * @param b
     * @param path
     * @return 
     */
    public final User setDriverPath(Browser b, String path){
        if(path == null){
            throw new NullPointerException("No path specified");
        }
        
        webDrivers.put(b, path);
        System.setProperty(b.getDriverEnvVar(), path);
        
        return this;
    }
    
    public final String getDriverPath(Browser b){
        return webDrivers.get(b);
    }
    
    /**
     * Removes the given Browser's driver path from
     * the system properties.
     * This method should be used whenever an invalid driver path is entered.
     * 
     * @param b
     * @return 
     */
    public final User clearDriverPath(Browser b){
        webDrivers.remove(b);
        System.clearProperty(b.getDriverEnvVar());
        return this;
    }
    
    public final User clearAllDriverPaths() {
        webDrivers.keySet().forEach((browser)->{
            //calling clearDriverPath(browser) would throw a concurrent modification exception
            System.clearProperty(browser.getDriverEnvVar());
        });
        webDrivers.clear();
        return this;
    }
    
    public final void load(InputStream is) throws IOException{
        String fileText = FileReaderUtil.readStream(is);
        System.out.println(fileText);
    }
    public final void load(File f) throws FileNotFoundException, IOException{
        load(new FileInputStream(f));
    }
    public final void load() throws URISyntaxException, IOException{
        String path = new File(User.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        path = path.substring(0, path.lastIndexOf(File.separatorChar));
        path = path + File.separatorChar + FILE_NAME;
        load(new File(path));
    }
    
    public final void save(OutputStream os) throws IOException{
        os.write(toString().getBytes());
    }
    public final void save(File f) throws IOException{
        save(new FileOutputStream(f));
    }
    public final void save() throws URISyntaxException, IOException{
        String path = new File(User.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        path = path.substring(0, path.lastIndexOf(File.separatorChar));
        path = path + File.separatorChar + FILE_NAME;
        System.out.println("Path is " + path);
        System.out.println(toString());
        FileWriterUtil.writeToFile(new File(path), toString());
    }
    
    /**
     * Gets the key-value pairs of this user's properties.
     * Future versions will use JSON to store the data.
     * 
     * @return 
     */
    @Override
    public String toString(){
        StringBuilder ret = new StringBuilder();
        webDrivers.forEach((b, p)->{
            ret.append('\n').append('"').append(b.getDriverEnvVar()).append('"').append(": ").append('"').append(p).append('"');
        });
        return ret.toString();
    }
    
    public static void main(String[] args) throws URISyntaxException, IOException{
        User u = new User();
        u.setDriverPath(Browser.EDGE, "C:\\users");
        u.save();
    }
}
