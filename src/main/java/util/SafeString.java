package util;

import java.util.Arrays;

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
public class SafeString implements CharSequence{
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
    public SafeString(char[] vals){
        this(vals, vals.length);
    }
    public SafeString(){
        this(new char[0], DEFAULT_CAPACITY);
    }
    
    public boolean isEmpty(){
        return nextIdx == 0;
    }
    
    public final char[] toCharArray(){
        char[] ret = new char[nextIdx];
        System.arraycopy(val, 0, ret, 0, nextIdx);
        return ret;
    }
    
    @Override
    public int length() {
        return nextIdx;
    }

    @Override
    public char charAt(int index) {
        if(index >= nextIdx){
            throw new IndexOutOfBoundsException();
        }
        return val[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        char[] a = new char[end - start];
        System.arraycopy(val, start, a, 0, end - start);
        return new SafeString(a, end - start);
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
    public SafeString append(char other){
        return append(new char[]{other});
    }
    public SafeString append(SafeString s){
        return append(s.toCharArray());
    }
    
    /**
     * 
     * @param numChars
     * @return this, for chaining purposes
     */
    public SafeString removeFromStart(int numChars){
        if(numChars > nextIdx){
            numChars = nextIdx;
        }
        char[] temp = new char[capacity];
        int newNextIdx = nextIdx - numChars;
        System.arraycopy(val, numChars, temp, 0, newNextIdx);
        clearValue();
        nextIdx = newNextIdx;
        val = temp;
        return this;
    }
    
    public int indexOf(char searchFor, int startIdx){
        int idx = -1;
        //print();
        //System.out.println("Searching for " + searchFor);
        for(int i = startIdx; i < nextIdx && idx == -1; i++){
            if(val[i] == searchFor){
                idx = i;
            }
        }
        return idx;
    }
    public int indexOf(char searchFor){
        return indexOf(searchFor, 0);
    }
    
    public SafeString substring(int startIdx, int numChars){
        if(startIdx < 0 && numChars < 0){
            throw new IllegalArgumentException("Both parameters mustn't be negative");
        }
        if(startIdx + numChars > nextIdx){
            throw new IllegalArgumentException("Substring out of bounds of this SafeString");
        }
        char[] cs = new char[numChars];
        for(int i = 0; i < numChars; i++){
            cs[i] = val[i + startIdx];
        }
        SafeString ret = new SafeString(cs, numChars);
        return ret;
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
        nextIdx = 0;
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
        
        SafeString s2 = s.substring(0, s.nextIdx);
        s2.print();
    }
}
