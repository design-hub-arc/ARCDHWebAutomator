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
    private static String formatTemplate(String template){
        return template.replace("$(LAUNCHER)", FileSystem.JAR_FOLDER_PATH + File.separator + "Launcher.jar");
    }
    private static void writeToDesktop(String fileName, String contents) throws IOException{
        File f = new File(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + fileName);
        if(!f.exists()){
            f.createNewFile();
        }
        FileWriterUtil.writeToFile(f, contents);
    }
    
    private static void installBatch() throws IOException{
        String batchText = formatTemplate(FileReaderUtil.readStream(Installer.class.getResourceAsStream("/batchTemplate.bat")));
        System.out.println(batchText);
        writeToDesktop("ARCDHWebAutomator.bat", batchText);
    }
    private static void installBash() throws IOException{
        String bashText = formatTemplate(FileReaderUtil.readStream(Installer.class.getResourceAsStream("/scriptTemplate.sh")));
        System.out.println(bashText);
        writeToDesktop("ARCDHWebAutomator.sh", bashText);
    }
    
    public static void install() throws IOException{
        String os = System.getProperty("os.name");
        System.out.println("Operating system is " + os);
        if(os.toLowerCase().contains("windows")){
            installBatch();
        } else {
            installBash();
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
