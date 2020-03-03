package logging;

/**
 * The LoggerInterface interface should be implemented by any class
 which should output and store messages from the program.
 * @see ApplicationLog
 * @author Matt Crow
 */
public interface LoggerInterface {
    /**
     * Logs a message. 
     * @param s the message to log.
     */
    public abstract void log(String s);
    
    /**
     * Used to obtain a detailed log
     * of messages recorded by this log.
     * 
     * @return 
     */
    public abstract String getLog();
}
