package gui;

import javax.swing.JPanel;

/**
 *
 * @author Matt
 */
public class Page extends JPanel{
    private final Application host;
    private Runnable onDone;
    
    public Page(Application app){
        host = app;
        onDone = ()->{
            throw new UnsupportedOperationException();
        };
    }
    
    public final void setOnDone(Runnable r){
        onDone = r;
    }
    
    public final void prev(){
        host.prev();
    }
    public final void next(){
        host.next();
        onDone.run();
    }
}
