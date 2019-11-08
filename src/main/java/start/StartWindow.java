package start;

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
        setContentPane(new StartPane());
        revalidate();
        repaint();
    }
    
    public static void main(String[] args){
        new StartWindow();
    }
}
