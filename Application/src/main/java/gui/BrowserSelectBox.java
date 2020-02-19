package gui;

import application.ApplicationResources;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import util.Browser;

/**
 * The BrowserSelectBox is used by BrowserSelectionPage
 * to allow the user to select a browser from a list of
 * browsers.
 * 
 * @author Matt Crow
 */
public class BrowserSelectBox extends JPanel{
    private final BrowserSelectionPage parent;
    private final Browser forBrowser;
    private final ScrollableTextDisplay disp;
    private final JRadioButton selectThisBrowser;
    private final ArrayList<Runnable> selectionListeners;
    
    public BrowserSelectBox(BrowserSelectionPage inPage, Browser b){
        parent = inPage;
        forBrowser = b;
        setLayout(new BorderLayout());
        
        add(new JLabel(b.getName()), BorderLayout.PAGE_START);
        
        disp = new ScrollableTextDisplay();
        updateText();
        add(disp, BorderLayout.CENTER);
        
        selectThisBrowser = new JRadioButton();
        selectThisBrowser.addActionListener((e)->select());
        add(selectThisBrowser, BorderLayout.LINE_START);
        
        revalidate();
        repaint();
        
        selectionListeners = new ArrayList<>();
    }
    
    public JRadioButton getSelectionButton(){
        return selectThisBrowser;
    }
    
    public void select(){
        selectThisBrowser.setSelected(true);
        selectionListeners.forEach((r)->r.run());
    }
    
    public void addSelectionListener(Runnable r){
        selectionListeners.add(r);
    }
    
    public final void updateText(){
        // set the text based on whether or not the user has the webdriver installed
        ApplicationResources resources = parent.getApp().getResources();
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
