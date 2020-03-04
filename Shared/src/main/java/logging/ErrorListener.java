package logging;

/**
 * Classes which need to know when the Logger
 * logs an error message should implement this interface,
 * and add themselves to the Logger as an error listener.
 * 
 * @author Matt Crow
 */

public interface ErrorListener {
    /**
     * Fires whenever the Logger logs an error message.
     * @param errMsg the error message which was logged.
     */
    public void errorLogged(String errMsg);
    
    /**
     * Fired whenever the given Logger has its error log cleared
     */
    public void logCleared();
}
