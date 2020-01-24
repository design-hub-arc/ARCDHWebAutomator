package gui;

import javax.swing.JPanel;

/**
 *
 * @author Matt
 */
public class Page extends JPanel{
    private final ApplicationPane host;
    private Runnable onDone;
    
    public Page(ApplicationPane app){
        host = app;
        onDone = ()->{
            throw new UnsupportedOperationException();
        };
    }
    
    public ApplicationPane getHost(){
        return host;
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
