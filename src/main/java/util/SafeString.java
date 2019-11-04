package util;

/**
 * In case security is a concern, this class stores
 * the information it gathers as a character array 
 * instead of a String.
 * Since Strings cannot be deleted or changed,
 * that might cause security problems.
 * Arrays can be "erased" by overwriting all of their data.
 * 
 * @author Matt Crow
 */
public class SafeString {
    private char[] val;
    private int capacity;
    private int nextIdx;
    
    private static final int DEFAULT_CAPACITY = 100;
    
    public SafeString(char[] vals, int initialCapacity){
        if(initialCapacity < vals.length){
            initialCapacity = vals.length;
        }
        val = new char[initialCapacity];
        capacity = initialCapacity;
        nextIdx = vals.length;
        
        System.arraycopy(vals, 0, val, 0, nextIdx);
    }
    public SafeString(){
        this(new char[0], DEFAULT_CAPACITY);
    }
    
    public SafeString append(char[] other){
        if(other.length + nextIdx > capacity){
            char[] temp = new char[capacity * 2];
            System.arraycopy(val, 0, temp, 0, nextIdx);
            //clear old val
            for(int i = 0; i < nextIdx; i++){
                val[i] = ' ';
            }
            val = temp;
            capacity *= 2;
        }
        System.arraycopy(other, 0, val, nextIdx, other.length);
        nextIdx += other.length;
        
        return this;
    }
    
    public SafeString removeFromStart(int numChars){
        if(numChars > nextIdx){
            throw new IllegalArgumentException("Cannot remove characters past current size");
        }
        char[] temp = new char[capacity];
        System.arraycopy(val, numChars, temp, 0, nextIdx - numChars);
        clearValue();
        nextIdx -= numChars;
        val = temp;
        return this;
    }
    
    public boolean isEmpty(){
        return nextIdx == 0;
    }
    
    public void print(){
        for(int i = 0; i < nextIdx; i++){
            System.out.print(val[i]);
        }
        System.out.println();
    }
    
    public final void clearValue(){
        for(int i = 0; i < capacity; i++){
            val[i] = ' ';
        }
    }
    
    @Override
    public void finalize() throws Throwable{
        clearValue();
        super.finalize();
    }
    
    public static void main(String[] args){
        SafeString s = new SafeString(
            new char[]{'a', 'b', 'c', '\n', 'd', 'e', 'f'},
            3
        );
        s.append(new char[]{'g','h','i'});
        s.print();
        System.out.println("After removing");
        s.removeFromStart(4);
        s.removeFromStart(1);
        s.print();
    }
}
