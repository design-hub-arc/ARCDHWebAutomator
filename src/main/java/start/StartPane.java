package start;

import io.FileSelector;
import io.QueryFileReader;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import pages.AbstractPageTemplate;
import pages.AccountBalancePage;
import pages.GoogleSearch;
import util.Browser;

/**
 *
 * @author Matt
 */
public class StartPane extends JPanel{
    private Browser selectedBrowser;
    private AbstractPageTemplate selAutomation;
    private String webDriverPath;
    private File sourceFile;
    
    public StartPane(){
        selectedBrowser = Browser.CHROME;
        selAutomation = new GoogleSearch();
        webDriverPath = null;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(browserPanel());
        add(automationPanel());
        add(webDriverPanel());
        add(sourcePanel());
        
        JButton run = new JButton("Run automation");
        run.addActionListener((e)->{
            try{
                if(webDriverPath == null){
                    throw new Exception("Please select your web driver path");
                }
                if(sourceFile == null){
                    throw new Exception("Please select a data source file");
                }
                String data = new QueryFileReader().readFile(sourceFile);
                selAutomation.run(data);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(run, ex.getMessage());
            }
        });
        add(run);
    }
    
    private final JPanel browserPanel(){
        JPanel browserPanel = new JPanel();
        browserPanel.setLayout(new BorderLayout());
        browserPanel.add(new JLabel("Select Browser to use"), BorderLayout.PAGE_START);
        ButtonGroup bg = new ButtonGroup();
        
        //todo: check which browsers the user has installed
        JRadioButton chrome = new JRadioButton("Chrome");
        chrome.addActionListener((e)->{
            selectedBrowser = Browser.CHROME;
        });
        JRadioButton fireFox = new JRadioButton("FireFox");
        fireFox.addActionListener((e)->{
            selectedBrowser = Browser.FIRE_FOX;
        });
        
        bg.add(chrome);
        bg.add(fireFox);
        chrome.setSelected(true);
        
        JPanel browserSelection = new JPanel();
        browserSelection.add(chrome);
        browserSelection.add(fireFox);
        
        browserPanel.add(browserSelection, BorderLayout.CENTER);
        
        return browserPanel;
    }
    
    private final JPanel automationPanel(){
        JPanel autoPanel = new JPanel();
        autoPanel.setLayout(new BorderLayout());
        autoPanel.add(new JLabel("Select which automation to run"), BorderLayout.PAGE_START);
        
        ButtonGroup automations = new ButtonGroup();
        JRadioButton gs = new JRadioButton("Google searches");
        gs.addActionListener((e)->{
            selAutomation = new GoogleSearch();
        });
        JRadioButton actBal = new JRadioButton("Account Balances");
        actBal.addActionListener((e)->{
            selAutomation = new AccountBalancePage();
        });
        automations.add(gs);
        automations.add(actBal);
        gs.setSelected(true);
        JPanel autoSel = new JPanel();
        autoSel.add(gs);
        autoSel.add(actBal);
        autoPanel.add(autoSel, BorderLayout.CENTER);
        return autoPanel;
    }
    
    private final JPanel webDriverPanel(){
        JPanel webDriverPanel = new JPanel();
        webDriverPanel.setLayout(new BoxLayout(webDriverPanel, BoxLayout.Y_AXIS));
        
        webDriverPanel.add(new JLabel("Select your web driver"));
        
        JTextArea currentDriver = new JTextArea("no driver selected");
        currentDriver.setEditable(false);
        webDriverPanel.add(currentDriver);
        
        JButton choosePath = new JButton("Choose web driver");
        choosePath.addActionListener((e)->{
            findWebDriver();
            currentDriver.setText(webDriverPath);
        });
        webDriverPanel.add(choosePath);
        
        return webDriverPanel;
    }
    
    private final JPanel sourcePanel(){
        JPanel p = new JPanel();
        JButton fileChooser = new JButton("Choose data source");
        fileChooser.addActionListener((e)->{
            chooseFile();
        });
        p.add(fileChooser);
        return p;
    }
    
    private void findWebDriver(){
        JFileChooser choose = new JFileChooser();
        choose.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if(choose.showOpenDialog(choose) == JFileChooser.APPROVE_OPTION){
            webDriverPath = choose.getSelectedFile().getAbsolutePath();
            System.out.println(webDriverPath);
            //change this
            System.setProperty("webdriver.chrome.driver", webDriverPath);
        }
    }
    
    private void chooseFile(){
        FileSelector.chooseCsvFile((File f)->{
            sourceFile = f;
        });
    }
}
