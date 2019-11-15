package gui;

import automations.AbstractAutomation;
import java.awt.CardLayout;
import javax.swing.JPanel;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Matt
 */
public class Application extends JPanel{
    private static final String AUTO = "AUTO";
    private static final String DATA = "DATA";
    private static final String DRIVER = "DRIVER";
    private static final String RUN = "RUN"; //use this page to show program output
    
    private AbstractAutomation selAuto;
    private String fileText;
    private WebDriver driver;
    
    public Application(){
        CardLayout l = new CardLayout();
        setLayout(l);
        
        AutomationSelect auto = new AutomationSelect(this);
        InputFileSelect file = new InputFileSelect(this);
        DriverSelect driverSel = new DriverSelect(this);
        RunWindow run = new RunWindow(this);
        
        auto.setOnDone(()->{
            selAuto = auto.getSelected();
            file.setAuto(selAuto);
            l.show(this, DATA);
        });
        file.setOnDone(()->{
            fileText = file.getFileText();
            l.show(this, DRIVER);
        });
        driverSel.setOnDone(()->{
            driver = driverSel.getDriver();
            l.show(this, RUN);
            run.run(selAuto, fileText, driver);
        });
        add(auto, AUTO);
        add(file, DATA);
        add(driverSel, DRIVER);
        add(run, RUN);
    }
    
    public final void prev(){
        ((CardLayout)getLayout()).previous(this);
    }
    
    public final void next(){
        ((CardLayout)getLayout()).next(this);
    }
}
