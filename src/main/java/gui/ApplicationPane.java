package gui;

import automationTools.AbstractAutomation;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Matt
 */
public class ApplicationPane extends JPanel{
    private static final String AUTO = "AUTO";
    private static final String DATA = "DATA";
    private static final String DRIVER = "DRIVER";
    private static final String RUN = "RUN"; //use this page to show program output
    
    private final ApplicationWindow hostingWindow;
    private final JPanel middle;
    
    private AbstractAutomation selAuto;
    private String fileText;
    private Class<? extends WebDriver> driverClass;
    
    public ApplicationPane(ApplicationWindow inWindow){
        super();
        hostingWindow = inWindow;
        
        setLayout(new BorderLayout());
        
        //middle
        middle = new JPanel();
        CardLayout l = new CardLayout();
        middle.setLayout(l);
        
        AutomationSelect auto = new AutomationSelect(this);
        InputFileSelect file = new InputFileSelect(this);
        BrowserSelect driverSel = new BrowserSelect(this);
        RunWindow run = new RunWindow(this);
        
        auto.setOnDone(()->{
            selAuto = auto.getSelected();
            l.show(middle, DATA);
            file.setAuto(selAuto);
        });
        file.setOnDone(()->{
            l.show(middle, DRIVER);
            fileText = file.getFileText();
        });
        driverSel.setOnDone(()->{
            l.show(middle, RUN);
            driverClass = driverSel.getDriverClass();
            run.run(selAuto, fileText, driverClass);
        });
        middle.add(auto, AUTO);
        middle.add(file, DATA);
        middle.add(driverSel, DRIVER);
        middle.add(run, RUN);
        
        add(middle, BorderLayout.CENTER);
        
        //bottom
        JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout());
        bottom.add(new JButton("TODO: implement error logging into ApplicationPane"));
        add(bottom, BorderLayout.PAGE_END);
    }
    
    public ApplicationWindow getHostingWindow(){
        return hostingWindow;
    }
    
    public final void prev(){
        ((CardLayout)middle.getLayout()).previous(middle);
    }
    
    public final void next(){
        ((CardLayout)middle.getLayout()).next(middle);
    }
}
