package logging;

/**
 * The Logger interface should be implemented by any class
 * which should output and store messages from the program.
 * 
 * @author Matt Crow
 */
public interface Logger {
    public abstract void log(String s);
    public abstract String getLog();
}
