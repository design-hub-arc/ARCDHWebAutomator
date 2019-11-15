package util;

/**
 *
 * @author Matt
 */
public enum Browser {
    CHROME("Google Chrome", "https://chromedriver.chromium.org/downloads"), 
    FIRE_FOX("Firefox", "https://github.com/mozilla/geckodriver/releases"),
    EDGE("Microsoft Edge", "https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/#downloads");
    
    private final String name;
    private final String driverLink;
    
    private Browser(String n, String driverURL){
        name = n;
        driverLink = driverURL;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDriverLink(){
        return driverLink;
    }
}
