package gui;

import guiComponents.ScrollableTextDisplay;
import automationTools.AbstractAutomation;
import automationTools.QueryingAutomation;
import csv.CsvFile;
import csv.CsvFileException;
import csv.CsvFileRequirements;
import csv.CsvParser;
import io.FileSelector;
import io.FileReaderUtil;
import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import logging.Logger;

/**
 *
 * @author Matt
 */
public class InputFileSelect extends Page{
    private boolean accepted;
    private final JLabel autoText;
    private final ScrollableTextDisplay disp;
    private AbstractAutomation forAuto;
    private CsvFile selectedFile;
    
    public InputFileSelect(ApplicationPane app){
        super(app);
        autoText = new JLabel("No Automation selected");
        forAuto = null;
        selectedFile = null;
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
            FileSelector.chooseCsvFile("Select the data file", (f)->{
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
    
    public final void setAuto(Class<? extends AbstractAutomation> aClass){
        try {
            forAuto = aClass.newInstance();
            disp.clear();
            addText("Set automation to " + aClass.getName() + '\n');
            if(forAuto instanceof QueryingAutomation){
                autoText.setText("Select source file for " + forAuto.getName());
                disp.appendText(((QueryingAutomation)forAuto).getQueryFileReqs().getReqDesc() + "\n"); 
                accepted = false;
            } else {
                autoText.setText(forAuto.getName() + " doesn't need a query file to run");
                disp.appendText("No need to select a file.");
                selectedFile = null;
                accepted = true;
                next();
            }
        } catch (Exception ex) {
            Logger.logError("InputFileSelect.selAuto", ex);
        }
    }
    
    private void selectFile(File f){
        if(forAuto instanceof QueryingAutomation){
            accepted = false;
            try {
                CsvFileRequirements reqs = ((QueryingAutomation)forAuto).getQueryFileReqs();
                reqs.validateFile(f);
                accepted = true;
                String fileText = FileReaderUtil.readFile(f);
                disp.clear();
                disp.appendText(f.getName() + " was accepted! \n");
                selectedFile = CsvParser.toCsvFile(fileText).getSubfile(reqs.getReqHeaders());
                String reformatted = selectedFile.toString();
                addText(reformatted);
                Logger.clearFlags();
            } catch (CsvFileException ex){
                disp.appendText("The file was not accepted for the following reasons:\n");
                disp.appendText(ex.getMessage() + '\n');
                Logger.logError("InputFileSelect.selectFile", ex);
            } catch (Exception ex) {
                disp.appendText("Encountered this error: \n");
                disp.appendText(ex.getMessage() + '\n');
                Logger.logError("InputFileSelect.selectFile", ex);
            }    
        } else {
            accepted = true;
            autoText.setText(forAuto.getName() + " doesn't need a query file to run");
            disp.appendText("No need to select a file.");
            selectedFile = null;
        }
    }
    
    private void addText(String text){
        Logger.log("InputFileSelect.addText", text);
        disp.appendText(text);
    }
    
    public final CsvFile getSelectedFile(){
        return selectedFile;
    }
}
