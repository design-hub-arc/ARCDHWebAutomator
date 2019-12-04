package gui;

import automationTools.AbstractAutomation;
import java.awt.CardLayout;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JPanel;
import org.openqa.selenium.WebDriver;
import util.User;

/**
 *
 * @author Matt
 */
public class Application extends JPanel{
    private static final String AUTO = "AUTO";
    private static final String DATA = "DATA";
    private static final String DRIVER = "DRIVER";
    private static final String RUN = "RUN"; //use this page to show program output
    
    private final User user;
    private AbstractAutomation selAuto;
    private String fileText;
    private WebDriver driver;
    
    public Application(){
        user = new User();
        try {
            user.load();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        CardLayout l = new CardLayout();
        setLayout(l);
        
        AutomationSelect auto = new AutomationSelect(this);
        InputFileSelect file = new InputFileSelect(this);
        DriverSelect driverSel = new DriverSelect(this);
        RunWindow run = new RunWindow(this);
        
        auto.setOnDone(()->{
            selAuto = auto.getSelected();
            l.show(this, DATA);
            file.setAuto(selAuto);
        });
        file.setOnDone(()->{
            l.show(this, DRIVER);
            fileText = file.getFileText();
        });
        driverSel.setOnDone(()->{
            l.show(this, RUN);
            driver = driverSel.getDriver();
            run.run(selAuto, fileText, driver);
        });
        add(auto, AUTO);
        add(file, DATA);
        add(driverSel, DRIVER);
        add(run, RUN);
    }
    
    public final User getUser(){
        return user;
    }
    
    public final void prev(){
        ((CardLayout)getLayout()).previous(this);
    }
    
    public final void next(){
        ((CardLayout)getLayout()).next(this);
    }
}
