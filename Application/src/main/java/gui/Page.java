package gui;

import application.Application;
import javax.swing.JPanel;

/**
 *
 * @author Matt
 */
public class Page extends JPanel{
    private final Application forApp;
    private final ApplicationPane host;
    private Runnable onDone;
    
    public Page(ApplicationPane pane){
        forApp = pane.getApp();
        host = pane;
        onDone = ()->{
            throw new UnsupportedOperationException();
        };
    }
    
    public Application getApp(){
        return forApp;
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
