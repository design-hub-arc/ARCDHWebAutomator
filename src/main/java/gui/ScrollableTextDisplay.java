package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Matt
 */
public class ScrollableTextDisplay extends JPanel{
    private final JTextArea textArea;
    private final StringBuilder text;
    public ScrollableTextDisplay(String displayText){
        super();
        setLayout(new GridLayout(1, 1));
        text = new StringBuilder();
        text.append(displayText);
        
        textArea = new JTextArea(displayText);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setRows(4);
        textArea.setBackground(Color.white);
        JScrollPane p = new JScrollPane(textArea);
        p.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(p);
        revalidate();
        repaint();
    }

    ScrollableTextDisplay() {
        this("");
    }
    
    //https://stackoverflow.com/questions/1345902/jmenuitem-setminimumsize-doesnt-work
    @Override
    public Dimension getPreferredSize() {
        Dimension preferred = super.getPreferredSize();
        Dimension minimum = getMinimumSize();
        Dimension maximum = getMaximumSize();
        preferred.width = Math.min(Math.max(preferred.width, minimum.width), 
            maximum.width);
        preferred.height = Math.min(Math.max(preferred.height, minimum.height), 
            maximum.height);
        return preferred;
    }
    public void appendText(String newText){
        text.append(newText);
        textArea.setText(text.toString());
        repaint();
    }
    public void setText(String newText){
        clear();
        appendText(newText);
    }
    public void clear(){
        text.delete(0, text.length());
        textArea.setText("");
    }
}
