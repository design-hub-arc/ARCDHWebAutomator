package launcher;

import javax.swing.JFrame;

/**
 *
 * @author Matt Crow
 */
public class LauncherFrame extends JFrame{
    private final LauncherPane content;
    
    public LauncherFrame(){
        super();
        setTitle("ARCDH Web Automator Launcher");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        content = new LauncherPane();
        setContentPane(content);
        pack();
        setVisible(true);
        revalidate();
        repaint();
    }
    
    public LauncherPane getContent(){
        return content;
    }
}
