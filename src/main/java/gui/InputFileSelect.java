package gui;

import automationTools.AbstractAutomation;
import automationTools.QueryingAutomation;
import io.CsvFileException;
import io.FileSelector;
import io.QueryFileReader;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Matt
 */
public class InputFileSelect extends Page{
    private String fileText;
    private boolean accepted;
    private final JLabel autoText;
    private final ScrollableTextDisplay disp;
    private AbstractAutomation forAuto;
    
    public InputFileSelect(Application app){
        super(app);
        autoText = new JLabel("No Automation selected");
        forAuto = null;
        fileText = "";
        accepted = false;
        
        setLayout(new BorderLayout());
        add(autoText, BorderLayout.PAGE_START);
        disp = new ScrollableTextDisplay("***Program output appears here***\n");
        add(disp, BorderLayout.CENTER);
        
        JPanel bottom = new JPanel();
        
        JButton back = new JButton("Go Back");
        back.addActionListener((e)->{
            prev();
        });
        bottom.add(back);
        
        JButton select = new JButton("Select a file");
        select.addActionListener((e)->{
            FileSelector.chooseCsvFile((f)->{
                selectFile(f);
            });
        });
        bottom.add(select);
        
        JButton next = new JButton("Next");
        next.addActionListener((e)->{
            if(accepted){
                next();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid file");
            }
        });
        bottom.add(next);
        
        add(bottom, BorderLayout.PAGE_END);
    }
    
    public final void setAuto(AbstractAutomation aa){
        forAuto = aa;
        disp.clear();
        if(!(aa instanceof QueryingAutomation)){
            autoText.setText(aa.getName() + " doesn't need a query file to run");
            disp.appendText("No need to select a file.");
            fileText = "";
            accepted = true;
            next();
        } else {
            autoText.setText("Select source file for " + aa.getName());
            disp.appendText(((QueryingAutomation)aa).getQueryManager().getQueryFileReqs().getReqDesc() + "\n"); 
            accepted = false;
        }
    }
    
    private void selectFile(File f){
        if(forAuto instanceof QueryingAutomation){
            accepted = false;
            try {
                ((QueryingAutomation)forAuto).getQueryManager().getQueryFileReqs().validateFile(f);
                accepted = true;
                fileText = new QueryFileReader().readFile(f);
                disp.clear();
                disp.appendText(f.getName() + " was accepted! \n");
                disp.appendText(((QueryingAutomation)forAuto).getQueryManager().getQueryFileReqs().reformatFile(fileText));
            } catch (CsvFileException ex){
                disp.appendText("The file was not accepted for the following reasons:\n");
                disp.appendText(ex.getMessage());
            } catch (Exception ex) {
                disp.appendText("Encountered this error: ");
                disp.appendText(ex.getMessage());
                ex.printStackTrace();
            }    
        } else {
            accepted = true;
            autoText.setText(forAuto.getName() + " doesn't need a query file to run");
            disp.appendText("No need to select a file.");
            fileText = "";
        }
        
        
    }
    
    public final String getFileText(){
        return fileText;
    }
}
