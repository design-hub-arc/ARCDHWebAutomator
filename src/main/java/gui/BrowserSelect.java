package gui;

import application.ApplicationResources;
import io.FileSelector;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import util.Browser;

/**
 *
 * @author Matt
 */
public class BrowserSelect extends Page{
    private Browser currentBrowser;
    private Class<? extends WebDriver> driverClass;
    
    private HashMap<Browser, BrowserInfoBox> browserInfo;
    private final JPanel browserList;
    private final ButtonGroup browserButtons;
    
    private final ScrollableTextDisplay text;
    
    public BrowserSelect(ApplicationPane app) {
        super(app);
        driverClass = null;
        setLayout(new BorderLayout());
        
        //top
        add(new JLabel("Select which browser to use"), BorderLayout.PAGE_START);
        
        //middle
        JPanel middle = new JPanel();
        middle.setLayout(new GridBagLayout());
        
        //fill horizontally, arrange vertically
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        browserInfo = new HashMap<>();
        //top of middle
        //list browser options
        browserList = new JPanel();
        browserList.setLayout(new GridBagLayout());
        //make sure the user can only select one browser
        browserButtons = new ButtonGroup();
        
        for(Browser browser : Browser.values()){
            addBrowser(browser);
        }
        JScrollPane scrolly = new JScrollPane(browserList);
        scrolly.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrolly.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        middle.add(scrolly, gbc.clone());
        
        //bottom of middle
        text = new ScrollableTextDisplay("***Program output will appear here***\n");
        middle.add(text, gbc.clone());
        
        add(middle, BorderLayout.CENTER);
        
        //bottom
        JPanel bottom = new JPanel();
        
        JButton back = new JButton("Go back");
        back.addActionListener((e)->{
            prev();
        });
        bottom.add(back);
        
        JButton select = new JButton("Select WebDriver");
        select.addActionListener((e)->{
            selectDriver();
        });
        bottom.add(select);
        
        JButton clear = new JButton("Clear saved WebDrivers");
        clear.addActionListener((e)->{
            ApplicationResources.getInstance().clearAllDriverPaths();
            browserInfo.values().forEach((bi)->bi.updateText());
        });
        bottom.add(clear);
        
        JButton next = new JButton("Next");
        next.addActionListener((e)->{
            if(driverClass == null){
                JOptionPane.showMessageDialog(this, "Please select the WebDriver for your browser before continuing");
            } else {
                next();
            }
        });
        bottom.add(next);
        
        add(bottom, BorderLayout.PAGE_END);
    }
    
    private void addBrowser(Browser b){
        JPanel j = new JPanel();
        j.setLayout(new BorderLayout());
        
        JRadioButton selectThisBrowser = new JRadioButton();
        selectThisBrowser.addActionListener((e)->{
            //change this to selectbrowser function
            currentBrowser = b;
            driverClass = null;
        });
        browserButtons.add(selectThisBrowser);
        
        j.add(selectThisBrowser, BorderLayout.LINE_START);
        
        BrowserInfoBox box = new BrowserInfoBox(b);
        browserInfo.put(b, box);
        j.add(box, BorderLayout.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        browserList.add(j, gbc);
        
        if(b == Browser.CHROME){
            selectThisBrowser.setSelected(true);
            currentBrowser = Browser.CHROME;
            ApplicationResources resources = getHost().getHostingWindow().getRunningApplication().getResources();
            driverClass = (resources.hasWebDriver(Browser.CHROME) ? ChromeDriver.class : null);
        }
    }
    
    private void selectDriver(){
        if(System.getProperty(currentBrowser.getDriverEnvVar()) == null){
            FileSelector.chooseExeFile("Select your WebDriver for " + currentBrowser.getName(),(file)->{
                ApplicationResources.getInstance().loadWebDriver(currentBrowser, file.getAbsolutePath());
            });
        }
        try{
            switch(currentBrowser){
                case CHROME:
                    driverClass = ChromeDriver.class;
                    break;
                case FIRE_FOX:
                    driverClass = FirefoxDriver.class;
                    break;
                case EDGE:
                    driverClass = EdgeDriver.class;
                    break;
                default:
                    throw new UnsupportedOperationException("Invalid browser: " + currentBrowser.name());
            }
            BrowserInfoBox box = browserInfo.get(currentBrowser);
            if(box != null){
                box.updateText();
            }
        } catch(Exception e){
            text.appendText("Looks like something went wrong:\n");
            text.appendText(e.toString());
            text.appendText("\n");
            ApplicationResources.getInstance().clearDriverPath(currentBrowser);
            BrowserInfoBox box = browserInfo.get(currentBrowser);
            if(box != null){
                box.updateText();
            }
        }
    }
    
    public final Class<? extends WebDriver> getDriverClass(){
        return driverClass;
    }
}
