package gui;

import automationTools.AbstractAutomation;
import automationTools.AbstractPeopleSoftAutomation;
import automationTools.QueryingAutomation;
import java.awt.BorderLayout;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Matt
 */
public class RunWindow extends Page{
    private final ScrollableTextDisplay text;
    public RunWindow(Application app) {
        super(app);
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
        
        add(bottom, BorderLayout.PAGE_END);
    }
    
    public final void run(AbstractAutomation aa, String fileText, WebDriver driver){
        new Thread(){
            @Override
            public void run(){
                try{
                    text.setText("***Program output will appear here***\n");
                    aa.setLogger(text);
                    
                    if(aa instanceof QueryingAutomation){
                        ((QueryingAutomation)aa).setInputFile(fileText);
                    }
                    
                    aa.run(driver);
                } catch (Exception ex){
                    text.appendText("Something went wrong:\n");
                    text.appendText(ex.toString());
                    text.appendText(Arrays.toString(ex.getStackTrace()));
                }
            }
        }.start();
    }
}
