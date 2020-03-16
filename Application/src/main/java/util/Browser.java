package util;

/**
 *
 * @author Matt
 */
public enum Browser {
    CHROME("Google Chrome", "https://chromedriver.chromium.org/downloads", "webdriver.chrome.driver", "chromedriver"), 
    FIRE_FOX("Firefox", "https://github.com/mozilla/geckodriver/releases", "webdriver.gecko.driver", "geckodriver"),
    EDGE("Microsoft Edge", "https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/#downloads", "webdriver.edge.driver", "msedgedriver");
    
    private final String name;
    private final String driverLink;
    private final String driverEnvVar;
    private final String driverFileName;
    
    private Browser(String n, String driverURL, String driverEnv, String fileName){
        name = n;
        driverLink = driverURL;
        driverEnvVar = driverEnv;
        driverFileName = fileName;
    }
    
    /**
     * Returns the browser whose webdriver has the given file name, 
     * or null if no browser's driver has that name.
     * @param fileName the name of the webdriver file used by a browser <b>without any extension</b>
     * @return 
     */
    public static Browser getByDriverFileName(String fileName){
        Browser ret = null;
        for(Browser b : Browser.values()){
            if(b.getDriverFileName().equals(fileName)){
                ret = b;
            }
        }
        return ret;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDriverLink(){
        return driverLink;
    }
    
    public String getDriverEnvVar(){
        return driverEnvVar;
    }
    
    public String getDriverFileName(){
        return driverFileName;
    }
}
