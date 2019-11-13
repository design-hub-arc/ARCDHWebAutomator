package start;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Matt
 */
public class ScrollableTextDisplay extends JComponent{
    private final JTextArea textArea;
    private final StringBuilder text;
    public ScrollableTextDisplay(String displayText){
        super();
        text = new StringBuilder();
        text.append(displayText);
        
        textArea = new JTextArea(displayText);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane p = new JScrollPane();
        p.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        p.add(textArea);
        add(p);
        revalidate();
        repaint();
    }
    
    public void appendText(String newText){
        text.append(newText);
        textArea.setText(text.toString());
        repaint();
    }
    public void setText(String newText){
        text.delete(0, text.length());
        appendText(newText);
    }
}
