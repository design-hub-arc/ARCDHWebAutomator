package main;

import java.util.ArrayList;
import logging.ErrorLogger;
import logging.Logger;

/**
 *
 * @author Matt
 */
public class Updater {
    private final ArrayList<Logger> loggers;
    
    
    public Updater(){
        loggers = new ArrayList<>();
    }
    
    public void addLogger(Logger l){
        loggers.add(l);
    }
    
    public void writeOutput(String msg){
        loggers.forEach((logger)->logger.log(msg));
    }
    public void reportError(String msg){
        loggers.stream().filter((logger)->{
            return logger instanceof ErrorLogger;
        }).map((logger)->{
            return (ErrorLogger)logger;
        }).forEach((logger)->{
            logger.logError(msg);
        });
    }
    public void reportError(Exception ex){
        loggers.stream().filter((logger)->{
            return logger instanceof ErrorLogger;
        }).map((logger)->{
            return (ErrorLogger)logger;
        }).forEach((logger)->{
            logger.logError(ex);
        });
    }
}
