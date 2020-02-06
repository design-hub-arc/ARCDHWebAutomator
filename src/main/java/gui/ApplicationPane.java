package gui;

import application.Application;
import automationTools.AbstractAutomation;
import csv.CsvFile;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import logging.ApplicationLog;
import logging.ErrorLogger;
import org.openqa.selenium.WebDriver;
import logging.ErrorListener;

/**
 *
 * @author Matt
 */
public class ApplicationPane extends JPanel{
    private static final String AUTO = "AUTO";
    private static final String DATA = "DATA";
    private static final String DRIVER = "DRIVER";
    private static final String RUN = "RUN"; //use this page to show program output
    
    private final Application forApp;
    private final JPanel middle;
    
    private Class<? extends AbstractAutomation> selAuto;
    private CsvFile inputFile;
    private Class<? extends WebDriver> driverClass;
    
    public ApplicationPane(ApplicationWindow inWindow){
        super();
        forApp = inWindow.getApp();
        
        setLayout(new BorderLayout());
        
        //middle
        middle = new JPanel();
        CardLayout l = new CardLayout();
        middle.setLayout(l);
        
        AutomationSelect auto = new AutomationSelect(this);
        InputFileSelect file = new InputFileSelect(this);
        BrowserSelectionPage driverSel = new BrowserSelectionPage(this);
        RunWindow run = new RunWindow(this);
        
        auto.setOnDone(()->{
            selAuto = auto.getSelected();
            l.show(middle, DATA);
            file.setAuto(selAuto);
        });
        file.setOnDone(()->{
            l.show(middle, DRIVER);
            inputFile = file.getSelectedFile();
        });
        driverSel.setOnDone(()->{
            l.show(middle, RUN);
            driverClass = driverSel.getDriverClass();
            run.run(selAuto, inputFile, driverClass);
        });
        middle.add(auto, AUTO);
        middle.add(file, DATA);
        middle.add(driverSel, DRIVER);
        middle.add(run, RUN);
        
        add(middle, BorderLayout.CENTER);
        
        //bottom
        JPanel bottom = new JPanel();
        bottom.setLayout(new FlowLayout());
        JButton viewLogButton = new JButton("View Log");
        
        ApplicationLog log = inWindow.getApp().getLog();
        log.addErrorListener(new ErrorListener(){
            @Override
            public void errorLogged(ErrorLogger log, String msg){
                viewLogButton.setText("Encountered an error");
            }
            
            @Override
            public void logCleared(ErrorLogger log){
                viewLogButton.setText("View Log");
            };
        });
        viewLogButton.addActionListener((e)->{
            new LogViewer(log);
        });
        bottom.add(viewLogButton);
        add(bottom, BorderLayout.PAGE_END);
    }
    
    public Application getApp(){
        return forApp;
    }
    
    public final void prev(){
        forApp.getLog().clearFlags();
        ((CardLayout)middle.getLayout()).previous(middle);
    }
    
    public final void next(){
        forApp.getLog().clearFlags();
        ((CardLayout)middle.getLayout()).next(middle);
    }
}
