package csv;

/**
 *
 * @author Matt
 */
public class CsvFileException extends RuntimeException{
    public CsvFileException(String msg){
        super(msg);
    }
    public CsvFileException(){
        super();
    }
}
