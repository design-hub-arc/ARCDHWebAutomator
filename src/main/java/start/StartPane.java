package start;

import io.FileSelector;
import io.QueryFileReader;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
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
        JPanel browserPanel = new JPanel();
        browserPanel.setLayout(new BorderLayout());
        browserPanel.add(new JLabel("Select Browser to use"), BorderLayout.PAGE_START);
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
        
        browserPanel.add(browserSelection, BorderLayout.CENTER);
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(browserPanel, gbc);
        
        
        //select which automation to run
        JPanel autoPanel = new JPanel();
        autoPanel.setLayout(new BorderLayout());
        autoPanel.add(new JLabel("Select which automation to run"), BorderLayout.PAGE_START);
        
        ButtonGroup automations = new ButtonGroup();
        JRadioButton gs = new JRadioButton("Google searches");
        JRadioButton actBal = new JRadioButton("Account Balances");
        automations.add(gs);
        automations.add(actBal);
        gs.setSelected(true);
        JPanel autoSel = new JPanel();
        autoSel.add(gs);
        autoSel.add(actBal);
        autoPanel.add(autoSel, BorderLayout.CENTER);
        
        gbc.gridy = 1;
        add(autoPanel, gbc);
        
        //select web driver path
        gbc.gridy = 2;
        add(webDriverPanel(), gbc);
        
        
        JButton fileChooser = new JButton("Choose data source");
        fileChooser.addActionListener((e)->{
            chooseFile();
        });
        gbc.gridy = 3;
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
        gbc.gridy = 4;
        add(run, gbc);
    }
    
    private final JPanel webDriverPanel(){
        JPanel webDriverPanel = new JPanel();
        webDriverPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        
        webDriverPanel.add(new JLabel("Select your web driver"), gbc);
        
        JTextArea currentDriver = new JTextArea("no driver selected");
        currentDriver.setEditable(false);
        gbc.gridy = 1;
        webDriverPanel.add(currentDriver, gbc);
        
        JButton choosePath = new JButton("Choose web driver");
        choosePath.addActionListener((e)->{
            findWebDriver();
            currentDriver.setText(webDriverPath);
        });
        gbc.gridy = 2;
        webDriverPanel.add(choosePath, gbc);
        
        return webDriverPanel;
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
