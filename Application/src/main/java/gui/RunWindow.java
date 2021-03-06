package gui;

import guiComponents.ScrollableTextDisplay;
import automationTools.AbstractAutomation;
import automationTools.QueryingAutomation;
import csv.CsvFile;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import logging.Logger;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Matt
 */
public class RunWindow extends Page{
    private final ScrollableTextDisplay text;
    
    public RunWindow(ApplicationPane app) {
        super(app);
        
        setLayout(new BorderLayout());
        
        add(new JLabel("Please wait while the automation runs..."), BorderLayout.PAGE_START);
        
        text = new ScrollableTextDisplay("***Program output will appear here***\n");
        text.setBackground(Color.red);
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
        add(bottom, BorderLayout.PAGE_END);
    }
    
    public final void run(Class<? extends AbstractAutomation> aClass, CsvFile inputFile, Class<? extends WebDriver> driverClass){
        new Thread(){
            @Override
            public void run(){
                try{
                    Logger.clearFlags();
                    AbstractAutomation aa = aClass.newInstance();
                    text.setText("***Program output will appear here***\n");
                    Logger.addMessageListener(text);
                    
                    if(aa instanceof QueryingAutomation){
                        ((QueryingAutomation)aa).setQueryFile(inputFile);
                    }
                    
                    aa.run(driverClass);
                } catch (Exception ex){
                    Logger.logError("RunWindow.run", ex);
                }
                Logger.removeMessageListener(text);
            }
        }.start();
    }
}
