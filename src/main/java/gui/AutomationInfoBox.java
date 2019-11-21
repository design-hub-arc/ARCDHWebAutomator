package gui;

import automations.AbstractPeopleSoftAutomation;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Matt
 */
public class AutomationInfoBox extends JPanel{
    private final AbstractPeopleSoftAutomation auto;
    public AutomationInfoBox(AbstractPeopleSoftAutomation aa){
        auto = aa;
        setLayout(new BorderLayout());
        add(new JLabel(aa.getName()), BorderLayout.PAGE_START);
        ScrollableTextDisplay text = new ScrollableTextDisplay(aa.getDesc());
        add(text, BorderLayout.CENTER);
    }
    
    public final AbstractPeopleSoftAutomation getAutomation(){
        return auto;
    }
}
