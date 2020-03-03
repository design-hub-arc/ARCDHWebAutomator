package guiComponents;

import io.FileSystem;
import io.FileSelector;
import io.FileWriterUtil;
import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import logging.LoggerInterface;

/**
 *
 * @author Matt Crow
 */
public class LogViewer extends JDialog{
    private final String msg;
    
    public LogViewer(LoggerInterface log){
        super();
        msg = log.getLog();
        
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        setContentPane(content);
        
        content.add(new JLabel("This log will automatically be saved to " + FileSystem.LOG_FOLDER_PATH + " when the program is done"), BorderLayout.PAGE_START);
        
        ScrollableTextDisplay text = new ScrollableTextDisplay(msg);
        content.add(text, BorderLayout.CENTER);
        
        JPanel options = new JPanel();
        JButton close = new JButton("Close");
        close.addActionListener((e)->{
            dispose();
        });
        options.add(close);
        JButton save = new JButton("Download this message");
        save.addActionListener((e)->{
            save();
        });
        options.add(save);
        content.add(options, BorderLayout.PAGE_END);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    private void save(){
        FileSelector.createNewFile("Where do you want to save the log?", (newFile)->{
            try {
                FileWriterUtil.writeToFile(newFile, msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
