package application;

import gui.Application;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JFrame;

/**
 *
 * @author Matt
 */
public class StartWindow extends JFrame{
    public StartWindow(){
        super();
        setTitle("American River College Design Hub Web Automator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setVisible(true);
        
        Application a = new Application();
        setContentPane(a);
        /*
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we) {
                try {
                    a.getUser().save();
                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        */
        revalidate();
        repaint();
    }
    
    public static void main(String[] args){
        new StartWindow();
    }
}
