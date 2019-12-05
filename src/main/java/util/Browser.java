package util;

/**
 *
 * @author Matt
 */
public enum Browser {
    CHROME("Google Chrome", "https://chromedriver.chromium.org/downloads", "webdriver.chrome.driver"), 
    FIRE_FOX("Firefox", "https://github.com/mozilla/geckodriver/releases", "webdriver.gecko.driver"),
    EDGE("Microsoft Edge", "https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/#downloads", "webdriver.edge.driver");
    
    private final String name;
    private final String driverLink;
    private final String driverEnvVar;
    
    private Browser(String n, String driverURL, String driverEnv){
        name = n;
        driverLink = driverURL;
        driverEnvVar = driverEnv;
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
}
