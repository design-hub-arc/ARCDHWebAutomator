package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The ApplicationFolder class is used
 * to interface with the folder containing
 * resources utilized by the program, such as
 * webdrivers, plugins, and more.
 * This provides a single location where the program
 * can find the resources it needs,
 * minimizing the need for users to select files.
 * 
 * @author Matt Crow
 */
public final class ApplicationFolder {
    
    private final String userHome = System.getProperty("user.home");
    private final String companyFolderName = userHome + File.separator + "ARCDH";
    private final String applicationFolderName = companyFolderName + File.separator + "WebAutomator";
    private static ApplicationFolder instance;
    
    private ApplicationFolder(){
        if(instance != null){
            throw new RuntimeException("Cannot instantiate more than one instance of singleton class, use getInstance() instead of constructor");
        }
    }
    
    /**
     * Used to access the singleton instance of this class.
     * If the class has not yet been initialized, instantiates before returning.
     * 
     * @return the instance of this class
     */
    public static ApplicationFolder getInstance(){
        if(instance == null){
            instance = new ApplicationFolder();
        }
        return instance;
    }
    
    public boolean companyFolderExists(){
        Path p = Paths.get(companyFolderName);
        return Files.exists(p) && Files.isDirectory(p);
    }
    
    /**
     * Verifies that the folder used by
     * this program exists.
     * 
     * @return whether or not the folder exists
     */
    public boolean folderExists(){
        Path p = Paths.get(applicationFolderName);
        return Files.exists(p) && Files.isDirectory(p);
    }
    
    private void createFolder() throws IOException{
        if(!companyFolderExists()){
            System.out.println("Creating ARCDH folder at " + companyFolderName);
            Files.createDirectory(Paths.get(companyFolderName));
        }
        if(!folderExists()){
            System.out.println("Creating folder at " + applicationFolderName);
            Files.createDirectory(Paths.get(applicationFolderName));
        }
    }
    
    public static void main(String[] args){
        try {
            getInstance().createFolder();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("failed to create folder");
        }
    }
}
