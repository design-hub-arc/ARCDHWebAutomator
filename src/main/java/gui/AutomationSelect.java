package gui;

import automationSamples.TableTest;
import automationSamples.PurchaseOrderAutomation;
import automationSamples.RequisitionAutomation;
import automationSamples.AccountBalanceAutomation;
import automationSamples.PurchaseOrderInfoAutomation;
import automationSamples.GoogleSearch;
import automationTools.AbstractAutomation;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

/**
 * The page where users will select which automation
 * they wish to run. I will want to update this to search
 * for available automations instead of having to manually 
 * add them myself.
 * 
 * @author Matt Crow
 */
public class AutomationSelect extends Page{
    private AbstractAutomation selectedAutomation;
    
    public AutomationSelect(Application app){
        super(app);
        setLayout(new BorderLayout());
        add(new JLabel("Select an Automation to run"), BorderLayout.PAGE_START);
        
        //construct the list of automations to choose from
        JPanel list = new JPanel();
        list.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        //TODO: how do I want to allow plugins to add new Automations to this?
        AbstractAutomation[] aas = new AbstractAutomation[]{
            new GoogleSearch(),
            new TableTest(),
            new AccountBalanceAutomation(),
            new RequisitionAutomation(),
            new PurchaseOrderAutomation(),
            new PurchaseOrderInfoAutomation()
        };
        
        //pair buttons with automation info
        ButtonGroup bg = new ButtonGroup();
        JPanel j;
        JRadioButton b;
        for(AbstractAutomation aa : aas){
            j = new JPanel();
            j.setLayout(new BorderLayout());
            b = new JRadioButton();
            b.addActionListener((e)->{
                selectedAutomation = aa;
            });
            bg.add(b);
            j.add(b, BorderLayout.LINE_START);
            j.add(new AutomationInfoBox(aa), BorderLayout.CENTER);
            list.add(j, gbc.clone());
        }
        JScrollPane availAuto = new JScrollPane(list);
        add(availAuto, BorderLayout.CENTER);
        
        //bottom section
        JButton nextStep = new JButton("Next");
        nextStep.addActionListener((e)->{
            if(selectedAutomation == null){
                JOptionPane.showMessageDialog(this, "Please select an automation");
            } else {
                next();
            }
        });
        add(nextStep, BorderLayout.PAGE_END);
        
        revalidate();
        repaint();
    }
    
    public final AbstractAutomation getSelected(){
        return selectedAutomation;
    }
}
