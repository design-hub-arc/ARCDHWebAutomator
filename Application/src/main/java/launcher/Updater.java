package launcher;

import io.FileSystem;
import java.io.File;
import java.io.IOException;

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
    
    public static void main(String[] args) throws IOException{
        new Updater().run();
    }
}
