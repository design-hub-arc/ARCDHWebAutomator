package gui;

import application.Application;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 * Serves as the frame for the application.
 * 
 * @author Matt Crow
 */
public final class ApplicationWindow extends JFrame{
    private final Application forApp;
    
    public ApplicationWindow(Application runningApp){
        super();
        forApp = runningApp;
        forApp.listenToWindow(this);
        
        setTitle("American River College Design Hub Web Automator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // fullscreen
        setSize(
            (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), 
            (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration()).bottom
        );
        setVisible(true);
        
        ApplicationPane a = new ApplicationPane(this);
        setContentPane(a);
        
        revalidate();
        repaint();
    }
    
    /**
     * 
     * @return the Application which has opened this window. 
     */
    public Application getApp(){
        return forApp;
    }
}
