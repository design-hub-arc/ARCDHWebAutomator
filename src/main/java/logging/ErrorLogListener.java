package logging;

/**
 * Classes which need to know when an ErrorLogger
 * logs a message should implement this interface,
 * and add themselves as an errorLogListener to that
 * error logger.
 * 
 * @author matt Crow
 */

public interface ErrorLogListener {
    /**
     * Fires whenever the given log logs an error message.
     * @param log the Logger which logged an error.
     * @param msg the error message which was logged.
     */
    public void errorLogged(Logger log, String msg);
    
    /**
     * Fired whenever the given Logger has its error log cleared
     * @param log 
     */
    public void logCleared(Logger log);
}
