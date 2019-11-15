package gui;

import automations.AbstractAutomation;
import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 *
 * @author Matt
 */
public class Application extends JPanel{
    private static final String AUTO = "AUTO";
    private static final String DATA = "DATA";
    private static final String DRIVER = "DRIVER";
    private static final String REVIEW = "REVIEW";
    private static final String RUN = "RUN"; //use this page to show program output
    
    private AbstractAutomation selAuto;
    private String fileText;
    
    public Application(){
        CardLayout l = new CardLayout();
        setLayout(l);
        
        AutomationSelect auto = new AutomationSelect(this);
        InputFileSelect file = new InputFileSelect(this);
        DriverSelect driver = new DriverSelect(this);
        
        auto.setOnDone(()->{
            selAuto = auto.getSelected();
            file.setAuto(selAuto);
            l.show(this, DATA);
        });
        file.setOnDone(()->{
            fileText = file.getFileText();
            l.show(this, DRIVER);
        });
        add(auto, AUTO);
        add(file, DATA);
        add(driver, DRIVER);
    }
    
    public final void prev(){
        ((CardLayout)getLayout()).previous(this);
    }
    
    public final void next(){
        ((CardLayout)getLayout()).next(this);
    }
}
