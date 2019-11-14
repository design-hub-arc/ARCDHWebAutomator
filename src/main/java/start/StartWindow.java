package start;

import gui.Application;
import javax.swing.JFrame;

/**
 *
 * @author Matt
 */
public class StartWindow extends JFrame{
    public StartWindow(){
        super();
        setTitle("PeopleSoft Automation Utility");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);
        
        //setContentPane(new StartPane());
        setContentPane(new Application());
        
        revalidate();
        repaint();
    }
    
    public static void main(String[] args){
        new StartWindow();
    }
}
