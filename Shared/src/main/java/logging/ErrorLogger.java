package logging;

import logging.Logger;

/**
 * The ErrorLogger class is used to allow Loggers
 * to log Exceptions, differentiating them from
 * regular logs.
 * 
 * @author Matt Crow
 */
public interface ErrorLogger extends Logger{
    /**
     * This method should add a detailed description
     * of the given Exception to the text log.
     * Generally speaking, this method should forward
     * an error message to this class' logError(String)
     * method.
     * 
     * @param ex the Exception to log. 
     */
    public abstract void logError(Exception ex);
    
    /**
     * This method should add the given message
     * to its log, as well as performing any procedures
     * needed for encountering an error, such as
     * firing any attached ErrorListeners.
     * 
     * @param errMsg the textual representation of an error,
     * or just a warning message.
     */
    public abstract void logError(String errMsg);

    /**
     * Logs a non-error message. 
     * @param msg the message to log.
     */
    @Override
    public abstract void log(String msg);
    
    /**
     * Used to obtain a detailed log
     * of errors recorded by this log.
     * 
     * @return 
     */
    @Override
    public abstract String getLog();
    
    /**
     * Used to obtain whether or not this log
     * has logged an error.
     * 
     * @return whether or not an error has been encountered
     */
    public abstract boolean hasLoggedError();
}
