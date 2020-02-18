package launcher;

import gui.ScrollableTextDisplay;
import java.awt.GridLayout;
import javax.swing.JPanel;

/**
 *
 * @author Matt Crow
 */
public class LauncherPane extends JPanel{
    private final ScrollableTextDisplay text;
    public LauncherPane(){
        super();
        setLayout(new GridLayout(1, 1));
        text = new ScrollableTextDisplay();
        add(text);
    }
    
    public ScrollableTextDisplay getTextDisplay(){
        return text;
    }
}
