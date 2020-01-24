package application;

import gui.ApplicationWindow;

/**
 * Application serves as the entry point for
 * the program.
 * 
 * @author Matt Crow
 */
public class Application {
    private ApplicationWindow window;
    
    
    private static Application instance;
    
    private Application(){
        if(instance != null){
            throw new RuntimeException("Cannot instantiate more than one instance of Application. Use Application.getInstance() instead");
        }
        window = null;
    }
    
    public static Application getInstance(){
        if(instance == null){
            instance = new Application();
        }
        return instance;
    }
    
    public void setWindow(ApplicationWindow w){
        window = w;
    }
    
    public static void main(String[] args){
        Application app = getInstance();
        ApplicationWindow w = new ApplicationWindow(app);
        app.setWindow(w);
    }
}
