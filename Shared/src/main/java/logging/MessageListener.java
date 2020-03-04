package logging;

/**
 * The MessageListener interface should be implemented by any class
 * which should receive messages sent to the Logger. Any class implementing
 * this interface should call Logger.addMessageListener(this);
 * 
 * @author Matt Crow
 */
public interface MessageListener {
    /**
     * Fired whenever Logger logs a non-error message.
     * @param s the message logged by Logger.
     */
    public abstract void messageLogged(String s);
}
