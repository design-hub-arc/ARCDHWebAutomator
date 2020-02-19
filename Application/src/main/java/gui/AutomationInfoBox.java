package gui;

import automationTools.AbstractAutomation;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Matt
 */
public class AutomationInfoBox extends JPanel{
    private final AbstractAutomation auto;
    public AutomationInfoBox(AbstractAutomation aa){
        auto = aa;
        setLayout(new BorderLayout());
        add(new JLabel(aa.getName()), BorderLayout.PAGE_START);
        ScrollableTextDisplay text = new ScrollableTextDisplay(aa.getDesc());
        add(text, BorderLayout.CENTER);
    }
    
    public final AbstractAutomation getAutomation(){
        return auto;
    }
}
