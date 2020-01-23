package gui;

import application.ApplicationResources;
import automationTools.AbstractAutomation;
import java.awt.CardLayout;
import java.io.IOException;
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
    private Class<? extends WebDriver> driverClass;
    
    public Application(){
        try {
            ApplicationResources.getInstance().init();
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
            driverClass = driverSel.getDriverClass();
            run.run(selAuto, fileText, driverClass);
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
