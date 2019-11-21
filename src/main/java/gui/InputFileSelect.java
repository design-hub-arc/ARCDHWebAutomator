package gui;

import automations.AbstractPeopleSoftAutomation;
import io.CsvFileException;
import io.FileRequirements;
import io.FileSelector;
import io.QueryFileReader;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
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
    private AbstractPeopleSoftAutomation forAuto;
    
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
    
    public final void setAuto(AbstractPeopleSoftAutomation aa){
        forAuto = aa;
        disp.clear();
        if(aa.getFileReq().equals(FileRequirements.NO_REQ)){
            autoText.setText(aa.getName() + " doesn't need a query file to run");
            disp.appendText("No need to select a file.");
            fileText = "";
            accepted = true;
            next();
        } else {
            autoText.setText("Select source file for " + aa.getName());
            disp.appendText(aa.getFileReq().getReqDesc() + "\n"); 
            accepted = false;
        }
    }
    
    private void selectFile(File f){
        accepted = false;
        try {
            fileText = new QueryFileReader().readFile(f);
            forAuto.validateFile(fileText);
            accepted = true;
            disp.clear();
            disp.appendText(f.getName() + " was accepted! \n");
            disp.appendText(fileText);
        } catch (CsvFileException ex){
            disp.appendText("The file was not accepted for the following reasons:\n");
            disp.appendText(ex.getMessage());
        } catch (IOException ex) {
            disp.appendText("Encountered this error: ");
            disp.appendText(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public final String getFileText(){
        return fileText;
    }
}
