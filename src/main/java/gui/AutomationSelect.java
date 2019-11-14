package gui;

import automations.*;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Matt
 */
public class AutomationSelect extends JPanel{
    private AbstractAutomation selectedAutomation;
    
    public AutomationSelect(){
        super();
        setLayout(new BorderLayout());
        add(new JLabel("Select an Automation to run"), BorderLayout.PAGE_START);
        JPanel list = new JPanel();
        JScrollPane availAuto = new JScrollPane(list);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        AbstractAutomation[] aas = new AbstractAutomation[]{
            new GoogleSearch(),
            new TableTest(),
            new AccountBalanceAutomation(),
            new RequisitionAutomation(),
            new PurchaseOrderAutomation(),
            new PurchaseOrderInfoAutomation()
        };
        for(AbstractAutomation aa : aas){
            list.add(new AutomationInfoBox(aa));
        }
        add(availAuto, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    public static void main(String[] args){
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(new AutomationSelect());
        f.setVisible(true);
        f.revalidate();
        f.repaint();
    }
}
