package logging;

import java.util.ArrayList;

/**
 * The Logger class is a static class used to
 * provide a single place to send output, where
 * it will later be written to a file.
 * 
 * @author Matt Crow
 */
public class Logger {
    private static final StringBuilder LOG = new StringBuilder();
    private static final ArrayList<LoggerInterface> MSG_LISTENERS = new ArrayList<>();
    private static final ArrayList<ErrorListener> ERR_LISTENERS = new ArrayList<>();
    private static boolean errorFlag = false;
    
    public Logger(){
        throw new RuntimeException("Logger is a static class, so you needn't instantiate it");
    }
    
    /**
     * Registers the given object implementing LoggerInterface to
     * receive messages from the Logger: any message the Logger logs
     * will be sent to this LoggerInterface as well.
     * 
     * @param i the object to receive messages in addition to the Logger.
     */
    public static final void addMessageListener(LoggerInterface i){
        if(i == null){
            throw new NullPointerException("Cannot add null as a message listener");
        }
        MSG_LISTENERS.add(i);
    }
    
    /**
     * Registers the given object implementing ErrorListener to
     * receive error messages from the Logger: any error message the Logger logs
     * will be sent to this ErrorListener as well.
     * 
     * @param i the object to receive error messages in addition to the Logger.
     */
    public static final void addErrorListener(ErrorListener i){
        if(i == null){
            throw new NullPointerException("Cannot add null as an error listener");
        }
        ERR_LISTENERS.add(i);
    }
    
    /**
     * Removes an object from the list of objects which should receive messages
     * sent to the Logger.
     * 
     * @param i the object to remove from the message listener list
     * @return whether or not the given object was in the list to begin with
     */
    public static final boolean removeMessageListener(LoggerInterface i){
        if(i == null){
            throw new NullPointerException("Cannot remove null as a message listener");
        }
        boolean ret = MSG_LISTENERS.contains(i);
        if(ret){
            MSG_LISTENERS.remove(i);
        }
        return ret;
    }
    
    /**
     * Removes an object from the list of objects which should receive error messages
     * sent to the Logger.
     * 
     * @param i the object to remove from the error listener list
     * @return whether or not the given object was in the list to begin with
     */
    public static final boolean removeErrorListener(ErrorListener i){
        if(i == null){
            throw new NullPointerException("Cannot remove null as an error listener");
        }
        boolean ret = ERR_LISTENERS.contains(i);
        if(ret){
            ERR_LISTENERS.remove(i);
        }
        return ret;
    }
    
    /**
     * Logs the given message so it can later be written to a log file.
     * This message is also send to each message listener attached to
     * the Logger. If no message listeners are attached, writes the message
     * to standard output.
     * 
     * @param source an identifier used to easy debugging by showing where
     * the message originated. Generally speaking, you'll probably want this
     * identifier to show the class and method which wrote the message, just
     * to make it easy to locate.
     * @param msg the message to write.
     */
    public static final void log(String source, String msg){
        if(source == null){
            source = "UNKNOWN SOURCE";
        }
        if(msg == null){
            throw new NullPointerException("Cannot log null message");
        }
        String formattedMsg = String.format("[%s] %s", source, msg);
        LOG.append(formattedMsg).append('\n');
        
        if(MSG_LISTENERS.isEmpty()){
            System.out.println(formattedMsg);
        } else {
            MSG_LISTENERS.forEach((LoggerInterface log)->log.log(formattedMsg));
        }
    }
    
    /**
     * Logs the given error message so it can later be written to a log file.
     * This message is also send to each error listener attached to the Logger. 
     * If no error message listeners are attached, 
     * writes the message to standard error output.
     * Also sets the error flag of the Logger to true until Logger.clearFlags() is invoked.
     * 
     * @param source an identifier used to easy debugging by showing where
     * the message originated. Generally speaking, you'll probably want this
     * identifier to show the class and method which wrote the message, just
     * to make it easy to locate.
     * @param errMsg the message to write.
     */
    public static final void logError(String source, String errMsg){
        if(source == null){
            source = "UNKNOWN SOURCE";
        }
        if(errMsg == null){
            throw new NullPointerException("Cannot log null message");
        }
        
        errorFlag = true;
        String formattedMsg = String.format("[%s] Error: %s", source, errMsg);
        LOG.append(formattedMsg).append('\n');
        
        if(ERR_LISTENERS.isEmpty()){
            System.err.println(formattedMsg);
        } else {
            ERR_LISTENERS.forEach((errList)->errList.errorLogged(errMsg));
        }
    }
    
    /**
     * Logs the given exception's stack trace so it can later be written to a log file.
     * This stack trace is also send to each error listener attached to the Logger. 
     * If no error message listeners are attached, 
     * prints the exception's stack trace to standard error output.
     * Also sets the error flag of the Logger to true until Logger.clearFlags() is invoked.
     * 
     * @param source an identifier used to easy debugging by showing where
     * the message originated. Generally speaking, you'll probably want this
     * identifier to show the class and method which wrote the message, just
     * to make it easy to locate.
     * @param ex the exception to log
     */
    public static final void logError(String source, Exception ex){
        if(source == null){
            source = "UNKNOWN SOURCE";
        }
        if(ex == null){
            throw new NullPointerException("Cannot log null message");
        }
        
        StringBuilder errMsg = new StringBuilder();
        errMsg
            .append(ex.toString())
            .append('\n');
        for(StackTraceElement frame : ex.getStackTrace()){
            errMsg.append("- ").append(frame.toString()).append('\n');
        }
        logError(source, errMsg.toString());
    }
    
    /**
     * Clears the error flag for the Logger,
     * and notifies each error listener attached to the Logger.
     */
    public static final void clearFlags(){
        errorFlag = false;
        ERR_LISTENERS.forEach((errList)->errList.logCleared());
    }
    
    /**
     * 
     * @return whether or not the Logger has logged 
     * an error since its flags have been cleared
     */
    public static final boolean hasLoggedError(){
        return errorFlag;
    }
    
    /**
     * 
     * @return gets the contents of the Logger
     */
    public static final String getLog(){
        return LOG.toString();
    }
    
    /**
     * Deletes the contents of the Logger.
     */
    public static final void clearLog(){
        LOG.delete(0, LOG.length());
    }
}
