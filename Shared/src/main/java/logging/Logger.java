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
    
    public Logger(){
        throw new RuntimeException("Logger is a static class, so you needn't instantiate it");
    }
    
    /**
     * Logs the given message so it can be
     * @param source
     * @param msg 
     */
    public static final void log(String source, String msg){
        String formattedMsg = String.format("[%s] %s", source, msg);
        LOG.append(formattedMsg).append('\n');
        
        if(MSG_LISTENERS.isEmpty()){
            System.out.println(formattedMsg);
        } else {
            MSG_LISTENERS.forEach((LoggerInterface log)->log.log(formattedMsg));
        }
    }
}
