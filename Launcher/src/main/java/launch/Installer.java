package launch;

import io.FileReaderUtil;
import io.FileSystem;
import io.FileWriterUtil;
import java.io.File;
import java.io.IOException;

/**
 * This class handles the creation
 * of executable files used to run
 * the project. Later versions will
 * perform a more "professional" installation.
 * 
 * @author Matt Crow
 */
public class Installer {
    public static final Installer WINDOWS_INSTALLER = new Installer("batchTemplate.bat", "bat");
    public static final Installer UNIX_INSTALLER =    new Installer("scriptTemplate.sh", "sh");
    
    private final String templateName;
    private final String scriptPath;
    
    private Installer(String templateResourceName, String scriptExtension){
        templateName = templateResourceName;
        scriptPath = System.getProperty("user.home") + File.separatorChar + "Desktop" + File.separatorChar + "ARCDHWebAutomator." + scriptExtension;
    }
    
    private String getFormattedTemplate() throws IOException{
        String original = FileReaderUtil.readStream(Installer.class.getResourceAsStream("/" + templateName));
        return original.replace("$(LAUNCHER)", FileSystem.JAR_FOLDER_PATH + File.separator + "Launcher.jar");
    }
    
    private void writeToDesktop() throws IOException{
        File f = new File(scriptPath);
        if(!f.exists()){
            f.createNewFile();
        }
        f.setExecutable(true);
        FileWriterUtil.writeToFile(f, getFormattedTemplate());
    }
    
    public boolean isInstalled(){
        return new File(scriptPath).exists();
    }
    
    public static void install() throws IOException{
        String os = System.getProperty("os.name");
        System.out.println("Operating system is " + os);
        Installer i = null;
        if(os.toLowerCase().contains("windows")){
            i = WINDOWS_INSTALLER;
            System.out.println("Running Windows installer");
        } else {
            i = UNIX_INSTALLER;
            System.out.println("Running UNIX installer");
        }
        if(i != null){
            if(i.isInstalled()){
                System.out.println("Already installed");
            } else {
                i.writeToDesktop();
                System.out.println("Installation successful!");
            }
        }
    }
    
    public static void main(String[] args){
        try {
            install();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
