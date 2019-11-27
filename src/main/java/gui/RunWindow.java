package gui;

import automationTools.AbstractAutomation;
import automationTools.QueryingAutomation;
import java.awt.BorderLayout;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import logging.ErrorLogger;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Matt
 */
public class RunWindow extends Page{
    private final ScrollableTextDisplay text;
    private final ErrorLogger errorLog;
    private final JButton anyErrors;
    
    public RunWindow(Application app) {
        super(app);
        
        errorLog = new ErrorLogger();
        
        setLayout(new BorderLayout());
        
        add(new JLabel("Please wait while the automation runs..."), BorderLayout.PAGE_START);
        text = new ScrollableTextDisplay("***Program output will appear here***\n");
        add(text, BorderLayout.CENTER);
        
        JPanel bottom = new JPanel();
        
        JButton back = new JButton("Go Back");
        back.addActionListener((e)->{
            prev();
        });
        bottom.add(back);
        
        JButton finish = new JButton("Return to start");
        finish.addActionListener((e)->{
            next();
        });
        setOnDone(()->{
            //do nothing
        });
        bottom.add(finish);
        anyErrors = new JButton("No errors to report");
        anyErrors.addActionListener((e)->{
            errorLog.showPopup();
        });
        bottom.add(anyErrors);
        
        add(bottom, BorderLayout.PAGE_END);
        
        errorLog.setOnEncounterError((errMsg)->{
            anyErrors.setText("Encountered an error");
        });
    }
    
    public final void run(AbstractAutomation aa, String fileText, WebDriver driver){
        new Thread(){
            @Override
            public void run(){
                try{
                    anyErrors.setText("No errors to report");
                    errorLog.clear();
                    text.setText("***Program output will appear here***\n");
                    aa.setLogger(text);
                    aa.setErrorLogger(errorLog);
                    
                    if(aa instanceof QueryingAutomation){
                        ((QueryingAutomation)aa).setInputFile(fileText);
                    }
                    
                    aa.run(driver);
                } catch (Exception ex){
                    errorLog.log(ex);
                }
            }
        }.start();
    }
}
