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
    private static void installBatch() throws IOException{
        String batchText = FileReaderUtil.readStream(Installer.class.getResourceAsStream("/batchTemplate.bat"));
        System.out.println(batchText);
        batchText = batchText.replace("$(LAUNCHER)", FileSystem.JAR_FOLDER_PATH + File.separator + "Launcher.jar");
        System.out.println(batchText);
        File f = new File(System.getProperty("user.home") + File.separator + "Desktop\\ARCDHWebAutomator.bat");
        if(!f.exists()){
            f.createNewFile();
        }
        FileWriterUtil.writeToFile(f, batchText);
    }
    public static void install(){
        
    }
    
    public static void main(String[] args){
        try {
            installBatch();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
