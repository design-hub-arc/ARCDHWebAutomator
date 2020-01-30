package gui;

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
public class BrowserSelectionPage extends Page{
    private Browser currentBrowser;
    private Class<? extends WebDriver> driverClass;
    
    private HashMap<Browser, BrowserSelectBox> browserOptions;
    private final JPanel browserList;
    private final ButtonGroup browserButtons;
    
    public BrowserSelectionPage(ApplicationPane app) {
        super(app);
        driverClass = null;
        setLayout(new BorderLayout());
        
        //top
        add(new JLabel("Select which browser to use"), BorderLayout.PAGE_START);
        
        //middle
        
        browserOptions = new HashMap<>();
        //list browser options
        browserList = new JPanel();
        browserList.setLayout(new GridBagLayout());
        //make sure the user can only select one browser
        browserButtons = new ButtonGroup();
        
        for(Browser browser : Browser.values()){
            addBrowser(browser);
        }
        browserOptions.get(Browser.CHROME).select();
        
        JScrollPane scrolly = new JScrollPane(browserList);
        scrolly.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrolly.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        add(scrolly, BorderLayout.CENTER);
        
        //bottom
        JPanel bottom = new JPanel();
        
        JButton back = new JButton("Go back");
        back.addActionListener((e)->{
            prev();
        });
        bottom.add(back);
        
        JButton select = new JButton("Select WebDriver file");
        select.addActionListener((e)->{
            selectDriver();
        });
        bottom.add(select);
        
        JButton clear = new JButton("Clear saved WebDriver files");
        clear.addActionListener((e)->{
            getApp().getResources().clearAllDriverPaths();
            browserOptions.values().forEach((bi)->bi.updateText());
        });
        bottom.add(clear);
        
        JButton next = new JButton("Next");
        next.addActionListener((e)->{
            if(getApp().getResources().hasWebDriver(currentBrowser)){
                next();
            } else {
                JOptionPane.showMessageDialog(this, "Please select the WebDriver for your browser before continuing");
            }
        });
        bottom.add(next);
        
        add(bottom, BorderLayout.PAGE_END);
    }
    
    private void addBrowser(Browser b){
        BrowserSelectBox box = new BrowserSelectBox(this, b);
        box.addSelectionListener(()->{
            selectBrowser(b);
        });
        browserButtons.add(box.getSelectionButton());
        browserOptions.put(b, box);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        browserList.add(box, gbc);
    }
    
    private void selectBrowser(Browser b){
        currentBrowser = b;
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
    }
    
    private void selectDriver(){
        FileSelector.chooseExeFile("Select your WebDriver for " + currentBrowser.getName(),(file)->{
            getApp().getResources().loadWebDriver(currentBrowser, file.getAbsolutePath());
        });
        BrowserSelectBox box = browserOptions.get(currentBrowser);
        if(box != null){
            box.updateText();
        }
    }
    
    public final Class<? extends WebDriver> getDriverClass(){
        return driverClass;
    }
}
