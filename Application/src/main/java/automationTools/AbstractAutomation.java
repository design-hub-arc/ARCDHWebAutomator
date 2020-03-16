package automationTools;

import java.util.List;
import logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The AbstractAutomation class serves as
 * the base for every type of web automation.
 * Any behavior described in this class should
 * be used by every subclass; therefore, any
 * specialized behavior should be implemented
 * into subclasses.
 * 
 * @see AbstractQueryGatherAutomation
 * 
 * @author Matt Crow
 */
public abstract class AbstractAutomation {
    private final String name;
    private final String desc;
    private WebDriver driver;
    private WebDriverWait wait;
    private boolean running;
    
    /**
     * 
     * @param autoName the name of this automation
     * @param description a brief description of what the automation does,
     * which should allow the user to know what the automation does.
     */
    public AbstractAutomation(String autoName, String description){
        name = autoName;
        desc = description;
        driver = null;
        wait = null;
        running = false;
    }
    
    public final String getName(){
        return name;
    }
    
    /**
     * Returns the textual description
     * of what this automation does.
     * @return  
     */
    public final String getDesc(){
        return desc;
    }
    
    /**
     * Used to get the WebDriver currently
     * being used to perform automation,
     * if any.
     * 
     * @return this' driver.
     */
    public final WebDriver getDriver(){
        if(!running){
            throw new NullPointerException("Automation is not being run, so the driver is not set");
        }
        return driver;
    }
    
    /**
     * While the automation is being run,
     * this method can be used to obtain
     * the WebDriverWait associated with
     * the WebDriver being used to run the
     * automation.
     * 
     * @return the wait associated with this' driver.
     */
    public final WebDriverWait getWait(){
        if(!running){
            throw new NullPointerException("Automation is not being run, so the wait is not set");
        }
        return wait;
    }
    
    /**
     * Waits for an element in the webpage to load,
     * then returns it.
     * 
     * Since FireFoxDriver appears to not block while the
     * webpage is loading, using driver.findElement(By by)
     * will usually through a StaleElementException,
     * so this method circumvents this problem.
     * 
     * @param by the locator used to find the element
     * @return the WebElement found by the "by" parameter.
     */
    public final WebElement awaitFindElement(By by){
        if(wait == null || driver == null){
            throw new NullPointerException("process is not running, so the WebDriver isn't set");
        }
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }
    
    /**
     * Waits for elements in the webpage to load,
     * then returns them.
     * 
     * Since FireFoxDriver appears to not block while the
     * webpage is loading, using driver.findElement(By by)
     * will usually through a StaleElementException,
     * so this method circumvents this problem.
     * 
     * @param by the locator used to find the elements
     * @return the WebElements found by the "by" parameter.
     */
    public final List<WebElement> awaitFindElements(By by){
        if(wait == null || driver == null){
            throw new NullPointerException("process is not running, so the WebDriver isn't set");
        }
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }
    
    /**
     * Shuts down the WebDriver and wait used
     * by this automation. Only works if the
     * automation is currently being run. Note
     * that this method sets the 'running' flag
     * to false
     * 
     * @return this, for chaining purposes. 
     */
    private AbstractAutomation finish(){
        Logger.log("AbstractAutomation.finish", "Done running, quitting browser.");
        if(driver != null){
            driver.quit();
            driver = null;
        }
        wait = null;
        
        running = false;
        
        Logger.log("AbstractAutomation.finish", "Browser has been closed successfully.");
        
        return this;
    }
    
    /**
     * Performs the automation.
     * This process handles both setup and
     * cleanup.
     * 
     * @param driverClass the class of the webdriver to run
     * @return this, for chaining purposes
     * @throws java.lang.Exception if an error occurs during either launching the WebDriver or running the automation
     */
    public final AbstractAutomation run(Class<? extends WebDriver> driverClass) throws Exception{
        if(running){
            throw new UnsupportedOperationException("Cannot run automation, as it is already running");
        }
        running = true;
        Logger.log("AbstractAutomation.run", "Running " + getClass().getName());
        Logger.log("AbstractAutomation.run", "Attempting to create WebDriver from " + driverClass.getName());
        try{
            driver = driverClass.newInstance();
            wait = new WebDriverWait(driver, 10);
            Logger.log("AbstractAutomation.run", "Driver created successfully.");
            doRun();
            Logger.log("AbstractAutomation.run", "Automation completed successfully");
        } catch(IllegalAccessException | InstantiationException e){
            Logger.log("AbstractAutomation.run", "Unable to create dirver. Please see error log for details. Terminating process.");
            throw e;
        } finally {
            finish();
        }
        return this;
    }
    
    /**
     * This method should contain the process executed by the automation.
     * This method is called by the run() method.
     */
    public abstract void doRun();
}
