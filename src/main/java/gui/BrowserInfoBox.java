package gui;

import application.ApplicationResources;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import util.Browser;

/**
 *
 * @author Matt
 */
public class BrowserInfoBox extends JPanel{
    private final Browser forBrowser;
    private final ScrollableTextDisplay disp;
    
    public BrowserInfoBox(Browser b){
        forBrowser = b;
        setLayout(new BorderLayout());
        add(new JLabel(b.getName()), BorderLayout.PAGE_START);
        disp = new ScrollableTextDisplay();
        updateText();
        add(disp, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    public final void updateText(){
        // set the text based on whether or not the user has the webdriver installed
        ApplicationResources resources = ApplicationResources.getInstance();
        disp.clear();
        if(resources.hasWebDriver(forBrowser)){
            disp.appendText("This browser's WebDriver is currently saved to ");
            disp.appendText(resources.getWebDriverPath(forBrowser));
            disp.appendText(", but if you want to update this driver, you can download it from ");
        } else {
            disp.appendText("This browser's WebDriver has not been loaded yet. ");
            disp.appendText("If you have not yet downloaded the driver, you can do so here: ");
        }
        
        disp.appendText(forBrowser.getDriverLink());
        disp.appendText(".\n");
    }
    
    public final Browser getBrowser(){
        return forBrowser;
    }
}
