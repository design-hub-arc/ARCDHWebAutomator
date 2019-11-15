package gui;

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
    
    public BrowserInfoBox(Browser b){
        forBrowser = b;
        setLayout(new BorderLayout());
        add(new JLabel(b.getName()), BorderLayout.PAGE_START);
        ScrollableTextDisplay disp = new ScrollableTextDisplay();
        disp.appendText("You can download the WebDriver for this by going to\n");
        disp.appendText(b.getDriverLink());
        add(disp, BorderLayout.CENTER);
    }
    
    public final Browser getBrowser(){
        return forBrowser;
    }
}
