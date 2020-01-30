package gui;

import javax.swing.JPanel;
import logging.ApplicationLog;

/**
 *
 * @author Matt
 */
public class Page extends JPanel{
    private final ApplicationPane host;
    private final ApplicationLog log;
    private Runnable onDone;
    
    public Page(ApplicationPane app){
        host = app;
        log = app.getHostingWindow().getRunningApplication().getLog();
        onDone = ()->{
            throw new UnsupportedOperationException();
        };
    }
    
    public ApplicationPane getHost(){
        return host;
    }
    
    public final ApplicationLog getLog(){
        return log;
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
