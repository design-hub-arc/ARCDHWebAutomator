package gui;

import automations.AbstractAutomation;
import io.FileSelector;
import io.QueryFileReader;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Matt
 */
public class InputFileSelect extends JPanel{
    private String fileText;
    private final JLabel autoText;
    private AbstractAutomation forAuto;
    private Runnable onDone;
    
    public InputFileSelect(){
        super();
        autoText = new JLabel("No Automation selected");
        forAuto = null;
        fileText = "";
        onDone = ()->{
            throw new UnsupportedOperationException();
        };
        setLayout(new BorderLayout());
        add(autoText, BorderLayout.PAGE_START);
        ScrollableTextDisplay disp = new ScrollableTextDisplay("***Program output appears here***\n");
        add(disp, BorderLayout.CENTER);
        JButton select = new JButton("Select a file");
        select.addActionListener((e)->{
            FileSelector.chooseCsvFile((f)->{
                try {
                    fileText = new QueryFileReader().readFile(f);
                    disp.appendText(f.getName() + "\n");
                    disp.appendText(fileText);
                } catch (IOException ex) {
                    disp.appendText("Encountered this error: ");
                    disp.appendText(ex.getMessage());
                    ex.printStackTrace();
                }
            });
        });
        add(select, BorderLayout.PAGE_END);
    }
    
    public final void setAuto(AbstractAutomation aa){
        forAuto = aa;
        autoText.setText("Select source file for " + aa.getName());
    }
    
    public final String getFileText(){
        return fileText;
    }
    
    public final void setOnDone(Runnable r){
        onDone = r;
    }
}
