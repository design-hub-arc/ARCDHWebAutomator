package start;

import io.FileSelector;
import io.QueryFileReader;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import pages.AccountBalancePage;
import pages.GoogleSearch;

/**
 *
 * @author Matt
 */
public class StartPane extends JPanel{
    private String webDriverPath;
    private File sourceFile;
    
    public StartPane(){
        webDriverPath = null;
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        
        //browser selection
        ButtonGroup selectedBrowser = new ButtonGroup();
        
        //todo: check which browsers the user has installed
        JRadioButton chrome = new JRadioButton("Chrome");
        JRadioButton fireFox = new JRadioButton("FireFox");
        
        selectedBrowser.add(chrome);
        selectedBrowser.add(fireFox);
        chrome.setSelected(true);
        
        JPanel browserSelection = new JPanel();
        browserSelection.add(chrome);
        browserSelection.add(fireFox);
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(browserSelection, gbc);
        
        //select which automation to run
        ButtonGroup automations = new ButtonGroup();
        JRadioButton gs = new JRadioButton("Google searches");
        JRadioButton actBal = new JRadioButton("Account Balances");
        automations.add(gs);
        automations.add(actBal);
        gs.setSelected(true);
        JPanel autoSel = new JPanel();
        autoSel.add(gs);
        autoSel.add(actBal);
        
        gbc.gridy = 1;
        add(autoSel, gbc);
        
        JButton fileChooser = new JButton("Choose data source");
        fileChooser.addActionListener((e)->{
            chooseFile();
        });
        gbc.gridy = 2;
        add(fileChooser, gbc);
        
        JButton run = new JButton("Run automation");
        run.addActionListener((e)->{
            try{
            if(webDriverPath == null){
                findWebDriver();
            }
            if(sourceFile == null){
                chooseFile();
            }
            String data = new QueryFileReader().readFile(sourceFile);
            if(gs.isSelected()){
                new GoogleSearch().run(data);
            } else if(actBal.isSelected()){
                new AccountBalancePage().run(data);
            }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        gbc.gridy = 3;
        add(run, gbc);
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
