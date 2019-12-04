package gui;

import io.FileSelector;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import util.Browser;

/**
 *
 * @author Matt
 */
public class DriverSelect extends Page{
    private Browser currentBrowser;
    private WebDriver driver;
    private ScrollableTextDisplay text;
    
    public DriverSelect(Application app) {
        super(app);
        driver = null;
        setLayout(new BorderLayout());
        
        //top
        add(new JLabel("Select which browser to use, and download its WebDriver"), BorderLayout.PAGE_START);
        
        //middle
        JPanel middle = new JPanel();
        middle.setLayout(new GridBagLayout());
        
        //fill horizontally, arrange vertically
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        //top of middle
        //list browser options
        JPanel list = new JPanel();
        list.setLayout(new GridBagLayout());
        //make sure the user can only select one browser
        ButtonGroup bg = new ButtonGroup();
        JPanel j;
        JRadioButton b;
        for(Browser browser : Browser.values()){
            j = new JPanel();
            j.setLayout(new BorderLayout());
            b = new JRadioButton();
            b.addActionListener((e)->{
                currentBrowser = browser;
                driver = null;
            });
            bg.add(b);
            j.add(b, BorderLayout.LINE_START);
            j.add(new BrowserInfoBox(browser), BorderLayout.CENTER);
            list.add(j, gbc.clone());
        }
        JScrollPane scrolly = new JScrollPane(list);
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
        
        JButton next = new JButton("Next");
        next.addActionListener((e)->{
            if(driver == null){
                JOptionPane.showMessageDialog(this, "Please select the WebDriver for your browser");
            } else {
                next();
            }
        });
        bottom.add(next);
        
        add(bottom, BorderLayout.PAGE_END);
    }
    
    private void selectDriver(){
        if(System.getProperty(currentBrowser.getDriverEnvVar()) == null){
            FileSelector.chooseExeFile("Select your WebDriver for " + currentBrowser.getName(),(file)->{
                System.setProperty(currentBrowser.getDriverEnvVar(), file.getAbsolutePath());
                getHost().getUser().setDriverPath(currentBrowser, file.getAbsolutePath());
            });
        }
        try{
            switch(currentBrowser){
                case CHROME:
                    driver = new ChromeDriver();
                    break;
                case FIRE_FOX:
                    driver = new FirefoxDriver();
                    break;
                case EDGE:
                    driver = new EdgeDriver();
                    break;
                default:
                    throw new UnsupportedOperationException("Invalid browser: " + currentBrowser.name());
            }
            driver.manage().window().setSize(new Dimension(500, 500));
            driver.manage().window().setPosition(new Point(getX() + getWidth(), getY()));
            JOptionPane.showMessageDialog(this, "Looks like that worked! Please don't close the browser window!");
        } catch(Exception e){
            text.appendText("Looks like something went wrong:\n");
            text.appendText(e.toString());
            text.appendText("\n");
            System.clearProperty(currentBrowser.getDriverEnvVar());
        }
    }
    
    public final WebDriver getDriver(){
        return driver;
    }
}
